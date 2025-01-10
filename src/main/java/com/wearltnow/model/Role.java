package com.wearltnow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractModel{
    @Id
    String name;

    String description;

    @ManyToMany(fetch = FetchType.EAGER)
    Set<Permission> permissions;

}
