package com.wearltnow.constant;

/**
 * OrderStatus là class chứa các hằng số trạng thái đơn hàng được định nghĩa trước trong hệ thống.
 * Các hằng số này có thể được sử dụng để theo dõi tình trạng của đơn hàng.
 */
public final class OrderStatus {

    // Trạng thái đơn hàng đang chờ xử lý
    public static final String PENDING = "PENDING";

    // Trạng thái đơn hàng đã được xác nhận
    public static final String CONFIRMED = "CONFIRMED";

    // Trạng thái đơn hàng đang được vận chuyển
    public static final String SHIPPED = "SHIPPED";

    // Trạng thái đơn hàng đã được giao
    public static final String DELIVERED = "DELIVERED";

    // Trạng thái đơn hàng đã bị hủy
    public static final String CANCELED = "CANCELED";

    private OrderStatus() {}
}
