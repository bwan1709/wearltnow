package com.wearltnow.dto.response.user;

import lombok.Data;

import java.util.List;

@Data
public class UserGroupResponse {
    private Long id;
    private String name;
    private List<UserResponse> users;
}
