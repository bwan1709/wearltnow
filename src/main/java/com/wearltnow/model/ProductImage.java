package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Entity
@Table(name = "product_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class ProductImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long imageId;

    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;
}

