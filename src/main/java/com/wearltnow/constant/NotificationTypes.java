package com.wearltnow.constant;

/**
 * OrderStatus là class chứa các hằng số trạng thái đơn hàng được định nghĩa trước trong hệ thống.
 * Các hằng số này có thể được sử dụng để theo dõi tình trạng của đơn hàng.
 */
public final class NotificationTypes {
    // Thông báo đơn hàng
    public static final String ORDER_CONFIRM = "order_confirmation";
    // Thông báo het ton
    public static final String LOW_STOCK = "low_stock";
    private NotificationTypes() {}
}
