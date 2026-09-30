package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String firstName;
    private String email;
    private String role;
}