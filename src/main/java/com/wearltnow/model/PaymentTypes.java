package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "payment_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTypes extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long paymentTypeId;

    String name;

}
