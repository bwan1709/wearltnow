package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_comments")
@Data
public class ProductComment extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "rate")
    private Integer rate;

    @Column(name = "parent_id")
    private Long parentId;

    @PrePersist
    protected void onCreate() {
        setCreatedAt(LocalDateTime.now());
    }
}
