package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE categories SET deleted = true WHERE category_id = ?")
public class Category extends AbstractModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long categoryId;

    String name;
    String image;
    String description;
    Boolean status;
    String slug;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "categoryId")
    Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    List<Category> subCategories;

    @OneToMany(mappedBy = "category")
    List<Product> products;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    Supplier supplier;
}
