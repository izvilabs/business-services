package com.example.user.controller;

import com.example.user_service.dto.UserResponseDTO;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc; //
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@ActiveProfiles("test") // decide environment for run test(h2 database, mock configuration)

public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; //simulate api requests

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); //using convert to json string and java objects

    @Test // this is junit test
    void registerUser_ShouldCreateUser() throws Exception {

        UserResponseDTO.UserCreateDTO request = new UserResponseDTO.UserCreateDTO();
        //DTO object for sent data using create user
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("0771234567");
        request.setImageUrl("image.jpg");


        mockMvc.perform(
                        post("/api/v1/users/create")
                                .contentType(MediaType.APPLICATION_JSON) //tell media type to servise
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated()); //check expected status and return fail or successfully pass test case


        // Check whether user was actually saved
        assert userRepository
                .findByEmail("test@gmail.com")
                .isPresent();
    }

    @Test
    void registerUser_ShouldFail_WhenEmailAlreadyExists () throws Exception {

        UserResponseDTO.UserCreateDTO request = new UserResponseDTO.UserCreateDTO();
        User existingUser = new User();
        existingUser.setFirstName("Old");
        existingUser.setLastName("User");
        existingUser.setEmail("duplicate@gmail.com");
        existingUser.setPassword("password123");

        userRepository.save(existingUser);

        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("duplicate@gmail.com");
        request.setPassword("password123");
        request.setPhoneNumber("0771234567");

        mockMvc.perform(
                post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isConflict());
    }


}