 package com.keystone.keystone.dto;

import lombok.Data;

@Data
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;

    public UserResponse(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}