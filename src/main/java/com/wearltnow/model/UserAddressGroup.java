package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.sql.Timestamp;

@Entity
@Table(name = "user_address_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class UserAddressGroup extends AbstractModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    String toName;
    String toPhone;
    String toWardCode;
    String toAddress;
    Boolean isActive;
    Integer toDistrictId;
    String toWardName;
    String toDistrictName;
    Integer toProvinceId;
    String toProvinceName;
}
