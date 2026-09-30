package com.example.user_service.exception;

import com.example.user_service.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        ApiResponseDTO apiResponseDTO = ApiResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT)
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex){
        ApiResponseDTO apiResponseDTO = ApiResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ApiResponseDTO> handleUserBlockedException(UserBlockedException ex){
        ApiResponseDTO apiResponseDTO = ApiResponseDTO.builder()
                .message(ex.getMessage())
                .status(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleException(Exception ex){
        ApiResponseDTO apiResponseDTO = ApiResponseDTO.builder()
                .message("Internal server error" + ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
