package com.example.user_service.service;

import com.example.user_service.dto.ApiResponseDTO;
import com.example.user_service.dto.LoginRequestDTO;
import com.example.user_service.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public ResponseEntity<ApiResponseDTO>createUser(UserResponseDTO.UserCreateDTO userCreateDTO);
    public ResponseEntity<ApiResponseDTO>login(LoginRequestDTO loginRequestDTO);
    public ResponseEntity<ApiResponseDTO> refreshAccessToken(String refreshToken);
    public ResponseEntity<ApiResponseDTO>logout(String refreshToken);
}