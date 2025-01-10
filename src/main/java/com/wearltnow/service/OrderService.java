package com.wearltnow.service;

import com.wearltnow.constant.OrderStatus;
import com.wearltnow.constant.PaymentStatus;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.user.AddressRequest;
import com.wearltnow.dto.request.order.CheckoutRequest;
import com.wearltnow.dto.request.ghn.GhnOrderRequest;
import com.wearltnow.dto.response.ghn.GhnOrderResponse;
import com.wearltnow.dto.response.order.OrderResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.OrderMapper;
import com.wearltnow.model.*;
import com.wearltnow.repository.*;
import com.wearltnow.specification.OrderSpecification;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    GhnService ghnService;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    OrderDetailRepository orderDetailRepository;
    OrderMapper orderMapper;
    PaymentTypeRepository paymentTypeRepository;
    UserAddressGroupRepository userAddressGroupRepository;
    CartService cartService;
    ProductInventoryRepository productInventoryRepository;
    DiscountCodeService discountCodeService;
    DiscountCodeRepository discountCodeRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ProductService productService;


    @Transactional
    public OrderResponse createOrder(CheckoutRequest checkoutRequest, AddressRequest addressRequest) {
        try{
            List<CartItem> cartItems;
            if (checkoutRequest.getUserId() != null) {
                cartItems = cartService.getCartItems(checkoutRequest.getUserId());
            } else {
                cartItems = checkoutRequest.getItems();
            }

            // Kiểm tra và lấy thông tin địa chỉ
            UserAddressGroup userAddressGroup = getUserAddressGroup(checkoutRequest.getUserId(), addressRequest);

            // Lấy thông tin phương thức thanh toán
            PaymentTypes paymentType = getPaymentType(checkoutRequest.getPaymentMethodId());

            // Tạo và thiết lập đơn hàng
            Order order = initializeOrder(checkoutRequest.getUserId());

            // Tạo danh sách OrderDetail từ cartItems
            List<OrderDetail> orderDetails = createOrderDetails(cartItems, order);

            // Tính tổng số tiền của đơn hàng
            BigDecimal orderTotal = calculateTotalAmount(orderDetails);
            order.setOrderTotal(orderTotal);

            GhnOrderResponse ghnResponse = sendOrderToGhn(orderDetails, userAddressGroup, orderTotal, checkoutRequest.getServiceTypeId());
            BigDecimal shippingFee = ghnService.getShippingFee(ghnResponse.getData().getOrderCode());
            BigDecimal totalAmount = calculateTotalAmount(orderTotal, shippingFee);

            setupPaymentForOrder(order, paymentType, totalAmount);

            BigDecimal discountAmount = BigDecimal.ZERO;

            if (checkoutRequest.getDiscountCode() != null && !checkoutRequest.getDiscountCode().isEmpty()) {
                discountCodeRepository.findByCode(checkoutRequest.getDiscountCode())
                        .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_CODE_NOT_VALID));
                Optional<DiscountCode> discountCodeOpt = discountCodeService.findDiscountCodeByCode(checkoutRequest.getDiscountCode());

                if (discountCodeOpt.isPresent()) {
                    DiscountCode discountCode = discountCodeOpt.get();
                    if ("ALL".equals(discountCode.getUserGroup().getName().trim())) {
                        // Mã giảm giá áp dụng cho tất cả người dùng, không cần kiểm tra nhóm người dùng
                        discountAmount = discountCodeService.applyDiscount(discountCode, orderTotal);
                        order.setDiscountAmount(discountAmount);  // Cập nhật số tiền giảm vào order
                    } else {
                        if(checkoutRequest.getUserId() == null) {
                            throw new AppException(ErrorCode.DISCOUNT_CODE_NOT_VALID_FOR_THIS_USER);
                        }else {
                            User user = userRepository.findById(checkoutRequest.getUserId()).get();
                            if (discountCodeService.isDiscountValid(discountCode, user)) {
                                discountAmount = discountCodeService.applyDiscount(discountCode, orderTotal);
                                order.setDiscountAmount(discountAmount);  // Cập nhật số tiền giảm vào order
                            } else {
                                throw new AppException(ErrorCode.DISCOUNT_CODE_NOT_VALID);
                            }
                        }
                    }
                }
            }

            totalAmount = totalAmount.subtract(discountAmount);
            order.setDiscountAmount(discountAmount);
            order.setOrderAmount(totalAmount);

            handleGhnResponse(ghnResponse, shippingFee, order);
            if(checkoutRequest.getUserId() != null)
                cartService.removeCart(checkoutRequest.getUserId());
            // Chuyển đổi và trả về OrderResponse
            User admin = userService.findAdmin();
            notificationService.sendOrderConfirmationNotification(admin.getUserId(), "Có đơn hàng mới cần xác nhận: " +
                    order.getOrder_code());
            return createOrderResponse(order);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during order creation: " + e.getMessage(), e);
        }
    }

    public OrderResponse confirmOrder(Long orderId) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOTFOUND));
        order.setStatus(OrderStatus.CONFIRMED);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponseDTO(updatedOrder);
    }

//    public void cancelOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOTFOUND));
//
//        if (order.getStatus() == OrderStatus.CONFIRMED) {
//            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELED);
//        }
//        order.getOrderDetails().forEach(this::restoreInventory);
//
//        order.setStatus(OrderStatus.CANCELED);
//        orderRepository.save(order);
//    }
//
//    public List<OrderResponse> findAllOrders() {
//        List<Order> orders = orderRepository.findAll();
//        return orders.stream()
//                .map(orderMapper::toOrderResponseDTO) // Chuyển đổi Order sang OrderResponse
//                .collect(Collectors.toList());
//    }

    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOTFOUND));

        if (newStatus.equals(OrderStatus.CANCELED) && order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELED);
        }
        if (order.getStatus().equals(OrderStatus.CANCELED)) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELED);
        }
        order.setStatus(newStatus);

        if (newStatus.equals(OrderStatus.CANCELED)) {
            order.getOrderDetails().forEach(this::restoreInventory);
        }
        Order updatedOrder = orderRepository.save(order);

        // Trả về phản hồi đơn hàng đã cập nhật
        return orderMapper.toOrderResponseDTO(updatedOrder);
    }

    public OrderResponse clientUpdateOrderStatus(Long orderId, String newStatus, Long clientId) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOTFOUND));

        if (order.getUser() == null || order.getUser().getUserId() == null) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        else {
            if (!(order.getUser().getUserId().equals(clientId))) {
                throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
            }
            if (newStatus.equals(OrderStatus.CANCELED) && order.getStatus().equals(OrderStatus.CONFIRMED)) {
                throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELED);
            }
            order.setStatus(newStatus);

            if (newStatus.equals(OrderStatus.CANCELED)) {
                order.getOrderDetails().forEach(this::restoreInventory);
            }
            Order updatedOrder = orderRepository.save(order);
            return orderMapper.toOrderResponseDTO(updatedOrder);
        }
    }

    public boolean isValidOrderStatus(String currentStatus, String newStatus) {
        Map<String, Integer> statusOrder = new HashMap<>();
        statusOrder.put(OrderStatus.PENDING, 1);
        statusOrder.put(OrderStatus.CONFIRMED, 2);
        statusOrder.put(OrderStatus.SHIPPED, 3);
        statusOrder.put(OrderStatus.DELIVERED, 4);
        statusOrder.put(OrderStatus.CANCELED, 5);
        Integer currentStatusOrder = statusOrder.get(currentStatus.toUpperCase());
        Integer newStatusOrder = statusOrder.get(newStatus.toUpperCase());
        if (currentStatusOrder == null || newStatusOrder == null) {
            return false;
        }
        return newStatusOrder > currentStatusOrder;
    }

    public PageResponse<OrderResponse> getAllOrders(int page, int size, String orderCode, String status, BigDecimal minTotalAmount, BigDecimal maxTotalAmount) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // Tạo Specification với các bộ lọc
        Specification<Order> specification = Specification.where(OrderSpecification.hasOrderCode(orderCode))
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasTotalAmountGreaterThanOrEqual(minTotalAmount))
                .and(OrderSpecification.hasTotalAmountLessThanOrEqual(maxTotalAmount));

        var pageData = orderRepository.findAll(specification, pageable);

        return PageResponse.<OrderResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(orderMapper::toOrderResponseDTO).toList())
                .build();
    }


    public PageResponse<OrderResponse> findOrdersByUserId(Long userId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = orderRepository.findByUser_UserId(userId, pageable);
        return PageResponse.<OrderResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(orderMapper::toOrderResponseDTO).toList())
                .build();
    }

    private void updateInventory(CartItem cartItem) {
        productService.deleteAllProductCache();
        Product product = productRepository.findByProductIdAndDeletedFalse(cartItem.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));
        // Tìm ProductInventory dựa vào productInventoryId
        ProductInventory productInventory = productInventoryRepository.findByProductAndColorAndSize(
                        product, cartItem.getColor(), cartItem.getSize())
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOTFOUND));
            if (productInventory.getQuantity() < cartItem.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
        }

        productInventory.setQuantity(productInventory.getQuantity() - cartItem.getQuantity());
        productInventoryRepository.save(productInventory);
    }

    private void restoreInventory(OrderDetail orderDetail) {
        productService.deleteAllProductCache();

        // Kiểm tra xem ProductInventory đã tồn tại chưa
        ProductInventory productInventory = productInventoryRepository.findByProductAndColorAndSize(
                        orderDetail.getProduct(), orderDetail.getColor(), orderDetail.getSize())
                .orElseGet(() -> {
                    // Nếu chưa tồn tại, tạo mới ProductInventory
                    ProductInventory newInventory = new ProductInventory();
                    newInventory.setProduct(orderDetail.getProduct());
                    newInventory.setColor(orderDetail.getColor());
                    newInventory.setSize(orderDetail.getSize());
                    newInventory.setQuantity(0);
                    return newInventory;
                });

        // Cập nhật số lượng sản phẩm trong kho
        productInventory.setQuantity(productInventory.getQuantity() + orderDetail.getQuantity());

        // Lưu lại vào cơ sở dữ liệu
        productInventoryRepository.save(productInventory);
    }


    private UserAddressGroup getUserAddressGroup(Long userId, AddressRequest addressRequest) {
        // Nếu có userId, lấy địa chỉ từ CSDL
        if (userId != null) {
            return userAddressGroupRepository.findByUserUserIdAndIsActive(userId, true)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_ADDRESS_NOTFOUND));
        }
        // Nếu không có userId nhưng có addressRequest, chỉ tạo một đối tượng UserAddressGroup mà không lưu vào CSDL
        else if (addressRequest != null) {
            UserAddressGroup userAddressGroup = new UserAddressGroup();
            userAddressGroup.setToName(addressRequest.getToName());
            userAddressGroup.setToPhone(addressRequest.getToPhone());
            userAddressGroup.setToWardCode(addressRequest.getToWardCode());
            userAddressGroup.setToAddress(addressRequest.getToAddress());
            userAddressGroup.setToDistrictId(addressRequest.getToDistrictId());
            userAddressGroup.setIsActive(true); // Cần đặt thuộc tính này nhưng không lưu vào CSDL
            return userAddressGroup; // Trả về địa chỉ mà không lưu vào CSDL
        }
        throw new AppException(ErrorCode.USER_ADDRESS_NOTFOUND);
    }

    private PaymentTypes getPaymentType(Long paymentMethodId) {
        return paymentTypeRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment type not found"));
    }

    private Order initializeOrder(Long userId) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        if (userId != null) {
            order.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found")));
        }
        orderRepository.save(order);
        return order;
    }

    private List<OrderDetail> createOrderDetails(List<CartItem> cartItems, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            updateInventory(cartItem); // Cập nhật tồn kho

            // Tìm sản phẩm trong cơ sở dữ liệu
            Product product = productRepository.findByProductIdAndDeletedFalse(cartItem.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

            // Lấy giá từ bảng giá
            Double price = productService.getProductPrice(cartItem.getProductId()); // Sử dụng phương thức getProductPrice để lấy giá từ bảng giá

            // Tạo đối tượng OrderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setPrice(BigDecimal.valueOf(price)); // Đặt giá từ bảng giá
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setColor(cartItem.getColor());
            orderDetail.setSize(cartItem.getSize());
            orderDetail.setOrder(order);

            // Thêm vào danh sách orderDetails
            orderDetails.add(orderDetail);
        }

        // Gán danh sách OrderDetail vào Order
        order.setOrderDetails(orderDetails);

        // Lưu tất cả OrderDetails vào cơ sở dữ liệu cùng một lần
        orderDetailRepository.saveAll(orderDetails);

        return orderDetails;
    }


    private BigDecimal calculateTotalAmount(List<OrderDetail> orderDetails) {
            BigDecimal totalOrderAmount = orderDetails.stream()
                    .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return totalOrderAmount;
        }

    private void setupPaymentForOrder(Order order, PaymentTypes paymentType, BigDecimal totalAmount) {
        String paymentStatus = paymentType.getPaymentTypeId() == 1 ? PaymentStatus.UNPAID : PaymentStatus.WAITING;
        Payment payment = Payment.builder()
                .order(order)
                .paymentType(paymentType)
                .amount(totalAmount)
                .paymentStatus(paymentStatus)
                .build();
        order.setPayment(payment);
    }

    private GhnOrderResponse sendOrderToGhn(List<OrderDetail> orderDetails, UserAddressGroup userAddressGroup, BigDecimal totalAmount, int serviceTypeId) {
        GhnOrderRequest ghnOrderRequest = new GhnOrderRequest();
        ghnOrderRequest.setCod_amount(totalAmount.intValue());
        ghnOrderRequest.setItems(orderDetails.stream()
                .map(detail -> {
                    GhnProduct ghnProduct = new GhnProduct();
                    ghnProduct.setName(detail.getProduct().getName());
                    ghnProduct.setQuantity(detail.getQuantity());
                    ghnProduct.setPrice(detail.getPrice().intValue());
                    return ghnProduct;
                }).collect(Collectors.toList()));
        ghnOrderRequest.setService_type_id(serviceTypeId);
        if (userAddressGroup != null) {
            ghnOrderRequest.setTo_name(userAddressGroup.getToName());
            ghnOrderRequest.setTo_phone(userAddressGroup.getToPhone());
            ghnOrderRequest.setTo_ward_code(userAddressGroup.getToWardCode());
            ghnOrderRequest.setTo_address(userAddressGroup.getToAddress());
            ghnOrderRequest.setTo_district_id(userAddressGroup.getToDistrictId());
        }
        return ghnService.createOrder(ghnOrderRequest);
    }

    private void handleGhnResponse(GhnOrderResponse ghnResponse, BigDecimal shippingFee, Order order) {
        if (ghnResponse.getCode() != 200) {
            throw new RuntimeException("Failed to create order with GHN: " + ghnResponse.getMessage());
        }

        order.setShippingFee(shippingFee);
        order.setOrder_code(ghnResponse.getData().getOrderCode());
        order.setExpected_delivery_time(ghnResponse.getData().getExpected_delivery_time());
    }

    private OrderResponse createOrderResponse(Order order) {
        OrderResponse orderResponse = orderMapper.toOrderResponseDTO(order);
        orderResponse.setOrder_code(order.getOrder_code());
        orderResponse.setTotalOrder(order.getOrderTotal());
        orderResponse.setShippingFee(order.getShippingFee());
        orderResponse.setExpected_delivery_time(order.getExpected_delivery_time());
        orderResponse.setDiscountAmount(order.getDiscountAmount());
        return orderResponse;
    }

    private BigDecimal calculateTotalAmount(BigDecimal totalOrder, BigDecimal shippingFee) {
        return totalOrder.add(shippingFee);
    }

    public Order getOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
            throw new AppException(ErrorCode.ORDER_NOTFOUND);
        }
    }

    public OrderResponse findOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        // Check if the order is present
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();

            // Map the Order entity to OrderResponse DTO
            return orderMapper.toOrderResponseDTO(order);
        } else {
            // Throw exception if order not found
            throw new AppException(ErrorCode.ORDER_NOTFOUND);
        }
    }


    // Phương thức lấy thông tin người dùng từ DB
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
    }
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }
}
