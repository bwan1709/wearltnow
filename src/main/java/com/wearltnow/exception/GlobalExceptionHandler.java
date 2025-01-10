package com.wearltnow.exception;

import java.util.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.wearltnow.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    MessageSource messageSource;
    static String minAtributte = "min";

    // Throw all Exception we're not define
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handlingAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Validation
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ApiResponse<List<String>>> handlingValidation(MethodArgumentNotValidException exception) {
//        List<String> errorMessages = new ArrayList<>();
//        Locale locale = LocaleContextHolder.getLocale(); // Lấy locale hiện tại
//        BindingResult bindingResult = exception.getBindingResult(); // Lấy BindingResult
//        Map attributes = null; // Khởi tạo attributes
//
//        for (ObjectError error : bindingResult.getAllErrors()) {
//            String messageKey = error.getDefaultMessage(); // Lấy key từ DefaultMessage
//            if (error instanceof FieldError fieldError) {
//                var constraintViolation = fieldError.unwrap(ConstraintViolation.class);
//                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
//            }
//            String errorMessage = messageSource.getMessage(messageKey, null, locale);
//            if (attributes.containsKey("min") && attributes.get("min") != null) {
//                errorMessage = errorMessage.replace("{" + minAtributte + "}", attributes.get("min").toString());
//            }
//            errorMessages.add(errorMessage); // Thêm thông báo lỗi vào danh sách
//        }
//
//        // Tạo đối tượng ApiResponse
//        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
//        apiResponse.setCode(ErrorCode.VALIDATION_ERROR.getCode()); // Sử dụng mã lỗi chung
//        apiResponse.setMessage(ErrorCode.VALIDATION_ERROR.getMessage(messageSource, locale));
//        apiResponse.setErrors(errorMessages); // Đặt danh sách lỗi vào trường errors
//
//        return ResponseEntity.badRequest().body(apiResponse); // Trả về phản hồi với mã lỗi 400
//    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Map<String, String>>> handlingValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errorDetails = new HashMap<>(); // Lưu lỗi theo định dạng {field: message}
        Locale locale = LocaleContextHolder.getLocale(); // Lấy locale hiện tại
        BindingResult bindingResult = exception.getBindingResult(); // Lấy BindingResult

        for (ObjectError error : bindingResult.getAllErrors()) {
            String messageKey = error.getDefaultMessage(); // Lấy key từ DefaultMessage
            String errorMessage = messageSource.getMessage(messageKey, null, locale); // Lấy thông báo lỗi

            if (error instanceof FieldError fieldError) {
                errorDetails.put(fieldError.getField(), errorMessage); // Đặt {field: message} vào errorDetails
            }
        }

        // Tạo đối tượng ApiResponse
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.VALIDATION_ERROR.getCode()); // Sử dụng mã lỗi chung
        apiResponse.setMessage(ErrorCode.VALIDATION_ERROR.getMessage(messageSource, locale)); // Thông điệp chung
        apiResponse.setErrors(errorDetails); // Đặt lỗi dưới dạng Map {field: message}

        return ResponseEntity.badRequest().body(apiResponse); // Trả về phản hồi với mã lỗi 400
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    ResponseEntity<ApiResponse> handlingHttpClientErrorException(HttpClientErrorException e) {
        ErrorCode errorCode = ErrorCode.GHN_ERROR;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
