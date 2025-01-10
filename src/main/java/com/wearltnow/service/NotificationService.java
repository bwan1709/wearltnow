package com.wearltnow.service;

import com.wearltnow.constant.NotificationTypes;
import com.wearltnow.dto.response.notify.NotificationResponse;
import com.wearltnow.dto.response.auth.RoleResponse;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.Notification;
import com.wearltnow.model.User;
import com.wearltnow.repository.NotificationRepository;
import com.wearltnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendLowStockNotifyToAdmin(Long adminId, String message) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.DIRECTOR_NOTFOUND));
        Notification notification = new Notification();
        notification.setUser(admin);
        notification.setMessage(message);
        notification.setType(NotificationTypes.LOW_STOCK);

        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/admin-orders", message);
    }

    public void sendOrderConfirmationNotification(Long adminId, String message) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new AppException(ErrorCode.DIRECTOR_NOTFOUND));
        Notification notification = new Notification();
        notification.setUser(admin);
        notification.setMessage(message);
        notification.setType(NotificationTypes.ORDER_CONFIRM);

        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/admin-orders", message);
    }

    // Thêm thông báo mới
    public Notification addNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Lấy tất cả thông báo cho một người dùng cụ thể
    public List<NotificationResponse> getAllNotificationsForUser(Long userId) {

        List<Notification> notifications = notificationRepository.findByUser_UserId(userId);

        return notifications.stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
    }

    public NotificationResponse updateNotification(Long id, Notification updatedNotification) {
        // Tìm thông báo theo ID, nếu không có sẽ throw exception
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // Cập nhật thông tin
        existingNotification.setMessage(updatedNotification.getMessage());
        existingNotification.setType(updatedNotification.getType());
        existingNotification.setStatus(updatedNotification.getStatus());
        if (updatedNotification.getUser() != null) {
            existingNotification.setUser(updatedNotification.getUser());
        }
        Notification savedNotification = notificationRepository.save(existingNotification);

        // Chuyển đổi sang NotificationResponse và trả về
        return convertToNotificationResponse(savedNotification);
    }


    private NotificationResponse convertToNotificationResponse(Notification notification) {
        UserResponse userResponse = convertToUserResponse(notification.getUser());

        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .user(userResponse)
                .status(notification.getStatus())
                .build();
    }

    private UserResponse convertToUserResponse(User user) {
        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .lastname(user.getLastname())
                .firstname(user.getFirstname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .gender(user.getGender())
                .dob(user.getDob())
                .roles(roleResponses)
                .build();
    }


    // Xóa thông báo theo ID
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
