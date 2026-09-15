package com.example.consumer.model.dto;

import com.example.consumer.model.User;

import java.time.LocalDateTime;

public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String status;
    private LocalDateTime registeredAt;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.status = user.getStatus();
        response.registeredAt = user.getRegisteredAt();
        return response;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}
