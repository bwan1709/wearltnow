package com.wearltnow.model;


import com.wearltnow.model.enums.DiscountStatus;
import com.wearltnow.model.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "discount_codes")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE discount_codes SET deleted = true WHERE id = ?")
public class DiscountCode extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String code;  // Mã giảm giá

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    DiscountType type;  // Loại mã giảm giá (phần trăm, giá trị cố định)

    @Column(nullable = false)
    BigDecimal amount;  // Giá trị giảm (phần trăm hoặc cố định)

    @Column
    BigDecimal minOrderValue;  // Giá trị đơn hàng tối thiểu để áp dụng mã giảm giá

    @Column(nullable = false)
    LocalDateTime startDate;  // Ngày bắt đầu

    @Column(nullable = false)
    LocalDateTime endDate;  // Ngày hết hạn

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    DiscountStatus status;  // Trạng thái của mã giảm giá (active, inactive)

    @Column
    Long usageLimit;  // Giới hạn số lần sử dụng

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    UserGroup userGroup;  // Nhóm khách hàng áp dụng mã giảm giá

    @ManyToMany
    @JoinTable(
            name = "discount_code_users",
            joinColumns = @JoinColumn(name = "discount_code_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> users;
}
