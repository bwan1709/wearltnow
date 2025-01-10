package com.wearltnow.service;

import com.wearltnow.constant.PaymentStatus;
import com.wearltnow.dto.request.payment.PaymentRequest;
import com.wearltnow.dto.request.payment.QRRequest;
import com.wearltnow.dto.response.payment.PaymentResponse;
import com.wearltnow.dto.response.payment.PaymentStatusResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.Order;
import com.wearltnow.model.Payment;
import com.wearltnow.model.PaymentTypes;
import com.wearltnow.repository.OrderRepository;
import com.wearltnow.repository.PaymentRepository;
import com.wearltnow.repository.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;



@Service
@RequiredArgsConstructor
public class PaymentService {
    @Autowired
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final QRService qrService;
    private final RestTemplate restTemplate;

    //phan stop checkPay
    private ScheduledFuture<?> scheduledFuture;
    @Autowired

    private PaymentTypeRepository paymentTypeRepository;


    // Phương thức mới để tạo thanh toán từ PaymentRequest
    public PaymentResponse createPayment(Long orderId) {
        // Lấy thông tin đơn hàng từ orderId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOTFOUND));

        // Lấy thông tin thanh toán từ đơn hàng
        Payment payment = order.getPayment();

        // Tạo QRRequest từ thông tin thanh toán
        QRRequest qrRequest = new QRRequest();
        qrRequest.setPaymentId(payment.getPaymentId());
        qrRequest.setTotalAmount(payment.getAmount());
        qrRequest.setAddInfo(String.valueOf(payment.getPaymentId()));

        // Tạo QR code
        String qrCode = qrService.createQrCode(qrRequest);

        // Tính thời gian đếm ngược 15 phút
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);
        long remainingSeconds = Duration.between(LocalDateTime.now(), expirationTime).getSeconds();
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;

        // Tạo PaymentResponse và trả về
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(payment.getPaymentId());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setPaymentStatus(payment.getPaymentStatus());
        paymentResponse.setCreatedAt(payment.getCreatedAt());
        paymentResponse.setQrCode(qrCode);
        paymentResponse.setCountdownTime(String.format("%02d:%02d", minutes, seconds));

        return paymentResponse;
    }

    public PaymentStatusResponse getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime paymentCreatedAt = payment.getCreatedAt();

        if (payment.getPaymentStatus().equals(PaymentStatus.PAIDBYCREDITCARD)) {
            return new PaymentStatusResponse(PaymentStatus.PAIDBYCREDITCARD, "00:00", paymentCreatedAt, payment.getPaymentTime());
        }

        long duration = java.time.Duration.between(paymentCreatedAt, currentTime).getSeconds();
        long remainingSeconds = (15 * 60) - duration;

        String countdownTime;
        if (remainingSeconds <= 0) {
            countdownTime = "00:00"; // Thời gian đã hết
            payment.setPaymentStatus(PaymentStatus.UNPAID);
            paymentRepository.save(payment);
        } else {
            long minutes = remainingSeconds / 60;
            long seconds = remainingSeconds % 60;
            countdownTime = String.format("%02d:%02d", minutes, seconds);
        }

        // Trả về PaymentStatusResponse với paymentTime là null khi chưa có thời gian thanh toán
        return new PaymentStatusResponse(payment.getPaymentStatus(), countdownTime, paymentCreatedAt, null);
    }


    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate.");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static final String PAYMENT_CHECK_URL = "https://script.google.com/macros/s/AKfycbxEVjSKjFl3lgtSoPTseoyCfHBUCR47nTt-tRJ2ijXH8lb98x3G3OsfBpA1Vlg0u1fsXg/exec";
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    public void startPaymentCheck(Long paymentId) {
        System.out.println("Starting payment check for paymentId: " + paymentId);
        scheduledFuture = executorService.scheduleAtFixedRate(() -> {
            try {
                checkPayment(paymentId);
            } catch (Exception e) {
                System.err.println("Error during payment check: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopPaymentCheck() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
            System.out.println("Stopped payment check for paymentId");
        }
    }

    public PaymentStatusResponse checkPayment(Long paymentId) {
        System.out.println("Checking payment status for paymentId: " + paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(PAYMENT_CHECK_URL, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.getBody());

                if (!jsonObject.getBoolean("error")) {
                    JSONArray transactions = jsonObject.getJSONArray("data");

                    for (int i = 0; i < transactions.length(); i++) {
                        JSONObject transaction = transactions.getJSONObject(i);
                        String description = transaction.getString("Mô tả");
                        double paymentAmountDouble = transaction.getDouble("Giá trị");
                        BigDecimal paymentAmount = BigDecimal.valueOf(paymentAmountDouble);

                        // So sánh thông tin trả về với đơn hàng
                        if (description.equals(paymentId.toString()) &&
                                paymentAmount.compareTo(payment.getAmount()) == 0) {

                            // Cập nhật trạng thái và thời gian thanh toán
                            payment.setPaymentStatus(PaymentStatus.PAIDBYCREDITCARD);
                            payment.setPaymentTime(LocalDateTime.now());
                            paymentRepository.save(payment);

                            stopPaymentCheck();

                            System.out.println("Payment confirmed for paymentId: " + paymentId);
                            return new PaymentStatusResponse(PaymentStatus.PAIDBYCREDITCARD, "00:00", payment.getCreatedAt(), payment.getPaymentTime());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking payment status: " + e.getMessage());
        }

        // Trả về khi thanh toán chưa xác nhận
        return new PaymentStatusResponse(PaymentStatus.UNPAID, "Payment not found or not confirmed", payment.getCreatedAt(), null);
    }

    public List<PaymentTypes> getPaymentTypes() {
        return paymentTypeRepository.findAll();
    }

    public boolean updatePaymentStatus(Long paymentId, String paymentStatus) {
        // Tìm thanh toán theo ID
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setPaymentStatus(paymentStatus); // Cập nhật trạng thái
            paymentRepository.save(payment); // Lưu lại trong DB
            return true;
        }
        return false; // Không tìm thấy thanh toán
    }

    public boolean isValidPaymentStatus(String paymentStatus) {
        List<String> validStatuses = Arrays.asList(PaymentStatus.PAID, PaymentStatus.UNPAID,
                PaymentStatus.PAIDBYCASH, PaymentStatus.PAIDBYCREDITCARD);
        return validStatuses.contains(paymentStatus.toUpperCase());
    }
}
