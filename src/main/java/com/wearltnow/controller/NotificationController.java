package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.notify.NotificationResponse;
import com.wearltnow.model.Notification;
import com.wearltnow.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Endpoint để thêm thông báo mới
    @PostMapping("/add")
    public ResponseEntity<Notification> addNotification(@RequestBody Notification notification) {
        Notification createdNotification = notificationService.addNotification(notification);
        return ResponseEntity.ok(createdNotification);
    }

    // Endpoint để xem tất cả thông báo cho admin hoặc user
    @GetMapping("/all")
    public ApiResponse<List<NotificationResponse>> getAllNotifications(@RequestParam Long userId) {
        List<NotificationResponse> notifications = notificationService.getAllNotificationsForUser(userId);
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notifications)
                .build();
    }

    // Endpoint để xóa một thông báo theo ID
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.<String>builder()
                .message("Notification deleted")
                .build();
    }

    @PostMapping("/send")
    public ApiResponse<String> sendOrderConfirmationNotification(
            @RequestParam Long adminId,
            @RequestParam String message) {
        notificationService.sendOrderConfirmationNotification(adminId, message);
        return ApiResponse.<String>builder()
                .message("Notification sent to admin!")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<NotificationResponse> updateNotification(
            @PathVariable Long id,
            @RequestBody Notification updatedNotification) {
        NotificationResponse response = notificationService.updateNotification(id, updatedNotification);
        return ApiResponse.<NotificationResponse>builder()
                .result(response)
                .build();
    }

}