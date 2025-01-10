package com.wearltnow.model;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserRoleId implements Serializable {

    private Long userUserId;
    private String rolesName;
}
