package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "users_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserRole {

    @EmbeddedId
    UserRoleId id;

    @ManyToOne
    @MapsId("userUserId")
    @JoinColumn(name = "user_user_id")
    User user;

    @ManyToOne
    @MapsId("rolesName")
    @JoinColumn(name = "roles_name")
    Role role;
}