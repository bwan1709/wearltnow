package com.wearltnow.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


import java.util.Locale;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized Error", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID(1001, "Uncategorized Error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User already existed", HttpStatus.NOT_FOUND),
    USER_NOTFOUND(1003, "User Not Found", HttpStatus.NOT_FOUND),
    USER_NOT_EXISTED(1004, "User Not Existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1006, "Role Not Found", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1007, "Invalid Token", HttpStatus.UNAUTHORIZED),
    USERNAME_PASSWORD_INCORRECT(1008, "Username or Password Incorrect", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1009, "Email already existed", HttpStatus.CONFLICT),
    MAX_FILE_SIZE(1010, "Max file size is 2MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1011, "Only jpg, png, gif, bmp files are allowed", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAILED(1012, "Image Upload Failed", HttpStatus.INTERNAL_SERVER_ERROR),
    CATEGORY_NOTFOUND(1013, "Category Not Found", HttpStatus.NOT_FOUND),
    OTP_INVALID(1014, "OTP has been expired or invalid", HttpStatus.BAD_REQUEST),
    REGISTRATION_NOT_FOUND(1015, "Registration Not Found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_EXISTED(1016, "Email not existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOTFOUND(1017, "Product Not Found", HttpStatus.NOT_FOUND),
    ORDER_NOTFOUND(1018, "Order Not Found", HttpStatus.NOT_FOUND),
    USER_ADDRESS_NOTFOUND(1019, "User Address Not Found", HttpStatus.NOT_FOUND),
    IMAGE_DELETE_FAILED(1018,"Image Delete Failed", HttpStatus.INTERNAL_SERVER_ERROR),
    QR_CODE_GENERATION_FAILED(1019, "QR Code Generation Failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_NOT_FOUND(1020, "Payment Failed", HttpStatus.NOT_FOUND),
    PAYMENT_CHECK_FAILED(1021, "Payment Check Failed", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELED(1022, "Order Can't be Canceled", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_INVENTORY(1023, "Insufficient Inventory", HttpStatus.BAD_REQUEST),
    INVENTORY_NOTFOUND(1024, "Not enough item in inventory", HttpStatus.NOT_FOUND),
    CART_NOTFOUND(1025, "Cart Not Found", HttpStatus.NOT_FOUND),
    CART_ITEM_NOTFOUND(1026, "Cart Item Not Found", HttpStatus.NOT_FOUND),
    DISCOUNT_CODE_NOT_VALID_FOR_USER(1027, "Discount code is not valid for this user", HttpStatus.BAD_REQUEST),
    DISCOUNT_CODE_NOT_VALID(1028, "Discount code is not valid", HttpStatus.BAD_REQUEST),
    USER_GROUP_NOTFOUND(1029, "User Group Not Found", HttpStatus.NOT_FOUND),
    USER_GROUP_EXISTS(1030, "User Group Exists", HttpStatus.CONFLICT),
    DISCOUNT_CODE_EXISTS(1031, "Discount Code Exists", HttpStatus.BAD_REQUEST),
    SOME_USER_NOT_FOUND(1032, "Some user in list not found!!", HttpStatus.NOT_FOUND),
    SUPPLIER_NOT_FOUND(1033, "Supplier not found!!", HttpStatus.NOT_FOUND),
    DISCOUNT_CODE_NOT_VALID_FOR_THIS_USER(1034, "The discount code is not valid for this user!!", HttpStatus.BAD_REQUEST),
    DIRECTOR_NOTFOUND(1035, "Director Not Found", HttpStatus.NOT_FOUND),
    ORDER_ACCESS_DENIED(1036, "You have no permission to edit this order", HttpStatus.FORBIDDEN),
    PRODUCT_PRICE_NOT_FOUND(1037, "Product Price Not Found", HttpStatus.NOT_FOUND),
    PRICE_NOT_FOUND(1038, "Price Not Found", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CANCELED(1039, "Order already cancelled", HttpStatus.CONFLICT),
    PAYMENT_TYPE_NOTFOUND(1040,   "Payment type not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(1041, "Notifie not found", HttpStatus.NOT_FOUND),
    DISCOUNT_CODE_NOT_FOUND(1042, "Discount code not found", HttpStatus.NOT_FOUND),
    ORDER_STATUS_NOT_VALID(1043, "Order status not valid", HttpStatus.BAD_REQUEST),
    GHN_ERROR(1044, "GHN not support for shipping to this district, pls try later", HttpStatus.BAD_REQUEST),
    DISCOUNT_PRICE_NOT_FOUND(1045, "Discount price not found!!", HttpStatus.NOT_FOUND),

    VALIDATION_ERROR(422, "validation.error", HttpStatus.BAD_REQUEST), // Mã lỗi chung cho validation
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
    public String getMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(message, null, locale);
    }
}
