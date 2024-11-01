package com.example.userapi.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;

    public AuthResponseDto(String token) {
        this.accessToken = token;
    }
}