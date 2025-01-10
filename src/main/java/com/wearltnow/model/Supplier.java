package com.wearltnow.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(name = "suppliers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE suppliers SET deleted = true WHERE supplier_id = ?")
public class Supplier extends AbstractModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long supplierId;

    String description;
    String email;
    String contactPerson;
    String name;
    String website;
    String phone;
    String taxCode;

    @OneToMany(mappedBy = "supplier")
    List<Category> categories;
}

