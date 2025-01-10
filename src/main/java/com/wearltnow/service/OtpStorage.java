package com.wearltnow.service;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpStorage {
    private static final long OTP_EXPIRATION_TIME_SECONDS = 60;

    // Thay đổi kiểu Map để lưu trữ OtpEntry thay vì String
    private final Map<String, OtpEntry> otpStore = new HashMap<>();

    // Lưu mã OTP và thời gian tạo vào otpStore
    public void saveOtp(String email, String otp) {
        LocalDateTime now = LocalDateTime.now();
        otpStore.put(email, new OtpEntry(otp, now));
    }

    // Kiểm tra nếu OTP đã hết hạn (sau 60 giây)
    private boolean isOtpExpired(LocalDateTime otpTimestamp) {
        LocalDateTime now = LocalDateTime.now();
        return otpTimestamp.plusSeconds(OTP_EXPIRATION_TIME_SECONDS).isBefore(now);
    }

    // Xác thực OTP, trả về false nếu không tìm thấy hoặc mã OTP đã hết hạn
    public boolean validateOtp(String email, String otp) {
        OtpEntry otpEntry = otpStore.get(email);
        if (otpEntry == null) {
            return false;
        }
        // Kiểm tra thời gian hiệu lực của mã OTP
        if (isOtpExpired(otpEntry.getTimestamp())) {
            otpStore.remove(email); // Xóa OTP đã hết hạn
            return false;
        }

        return otp.equals(otpEntry.getOtp());
    }

    public void deleteOtp(String email) {
        otpStore.remove(email);
    }

    // Lớp lưu trữ thông tin mã OTP và thời gian tạo ra
    @Data
    private static class OtpEntry {
        private final String otp;
        private final LocalDateTime timestamp;

        public OtpEntry(String otp, LocalDateTime timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
