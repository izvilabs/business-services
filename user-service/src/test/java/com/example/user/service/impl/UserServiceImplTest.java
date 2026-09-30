package com.example.user.service.impl;

import com.example.user_service.dto.LoginRequestDTO;
import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.entity.RefreshToken;
import com.example.user_service.entity.User;
import com.example.user_service.enums.Role;
import com.example.user_service.exception.EmailAlreadyExistsException;
import com.example.user_service.exception.ResourceNotFoundException;
import com.example.user_service.exception.UserBlockedException;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JwtService;
import com.example.user_service.security.RefreshTokenService;
import com.example.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDTO.UserCreateDTO userCreateDTO;

    @BeforeEach
    void setUp() {

        userCreateDTO = new UserResponseDTO.UserCreateDTO();

        userCreateDTO.setEmail("test@gmail.com");
        userCreateDTO.setPassword("password123");
        userCreateDTO.setFirstName("Test");
        userCreateDTO.setLastName("User");
        userCreateDTO.setPhoneNumber("0771234567");
        userCreateDTO.setImageUrl("image.jpg");

        user = new User();

        user.setUserID(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("0771234567");
        user.setImageUrl("image.jpg");
        user.setRole(Role.CUSTOMER);
        user.setBlocked(false);
    }

    //test create user
    @Test
    void createUser_ShouldCreateUserSuccessfully() {


        when(userRepository.existsByEmail(userCreateDTO.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(userCreateDTO.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        ResponseEntity<?> response =
                userService.createUser(userCreateDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(userRepository)
                .existsByEmail(userCreateDTO.getEmail());

        verify(passwordEncoder)
                .encode(userCreateDTO.getPassword());

        verify(userRepository)
                .save(any(User.class));
    }


    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {


        when(userRepository.existsByEmail(userCreateDTO.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(userCreateDTO)
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void login_ShouldLoginSuccessfully() {


        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();

        loginRequestDTO.setEmail("test@gmail.com");
        loginRequestDTO.setPassword("password123");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateAccessToken(
                "1",
                "CUSTOMER"
        )).thenReturn("access-token");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        ResponseEntity<?> response =
                userService.login(loginRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(passwordEncoder)
                .matches("password123", "encodedPassword");

        verify(jwtService)
                .generateAccessToken("1", "CUSTOMER");

        verify(refreshTokenService)
                .createRefreshToken(user);
    }


    @Test
    void login_ShouldThrowException_WhenEmailNotFound() {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();

        loginRequestDTO.setEmail("unknown@gmail.com");
        loginRequestDTO.setPassword("password123");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.login(loginRequestDTO)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateAccessToken(anyString(), anyString());

        verify(refreshTokenService, never())
                .createRefreshToken(any(User.class));
    }


    @Test
    void login_ShouldThrowException_WhenUserIsBlocked() {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();

        loginRequestDTO.setEmail("test@gmail.com");
        loginRequestDTO.setPassword("password123");

        user.setBlocked(true);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserBlockedException.class,
                () -> userService.login(loginRequestDTO)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateAccessToken(anyString(), anyString());

        verify(refreshTokenService, never())
                .createRefreshToken(any(User.class));
    }


    @Test
    void login_ShouldThrowException_WhenPasswordIsWrong() {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();

        loginRequestDTO.setEmail("test@gmail.com");
        loginRequestDTO.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                UserBlockedException.class,
                () -> userService.login(loginRequestDTO)
        );

        verify(jwtService, never())
                .generateAccessToken(anyString(), anyString());

        verify(refreshTokenService, never())
                .createRefreshToken(any(User.class));
    }
}