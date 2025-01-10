package com.wearltnow.model;

import com.wearltnow.constant.NotificationStatuses;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "notifications")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends AbstractModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status = NotificationStatuses.UNREAD;
}
