package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.util.Set;

@Entity
@Table(name = "permissions")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends AbstractModel{
    @Id
    String name;
    String description;
}
