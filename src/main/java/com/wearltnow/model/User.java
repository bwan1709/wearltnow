package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE user_id = ?")
public class User extends AbstractModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    String username;
    String password;
    String lastname;
    String firstname;
    String phone;
    String email;
    Boolean gender;
    LocalDate dob;

    @OneToMany(mappedBy = "user")
    List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserAddressGroup> addresses;

    @ManyToMany
    Set<Role> roles;

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    UserGroup userGroup;
}

