package com.m347.pollit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.repositories.PollRepository;
import com.m347.pollit.repositories.TokenRepository;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.LoginRequest;
import com.m347.pollit.requests.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll(); // DB leeren vor jedem Test

        UserEntity user = new UserEntity(
                "Max", "Muster", "test@mail.com",
                encoder.encode("secret")
        );
        userRepository.save(user);
        validRequest = new RegisterRequest();
        validRequest.setFirstname("Max");
        validRequest.setLastname("Muster");
        validRequest.setEmail("newuser@mail.com");
        validRequest.setPassword("safePW1234");
    }

    @Test
    void loginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("test@mail.com", "secret");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expires").exists())
                .andExpect(jsonPath("$.user.email").value("test@mail.com"));
    }

    @Test
    void loginWithWrongEmail() throws Exception {
        LoginRequest request = new LoginRequest("wrong@mail.com", "secret");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest("test@mail.com", "notSoSecret");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }


//    Register

    @Test
    void registerSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expires").exists())
                .andExpect(jsonPath("$.user.email").value("newuser@mail.com"));
    }

    @Test
    void registerFailsWhenEmailAlreadyExists() throws Exception {
        RegisterRequest duplicateEmailRequest = new RegisterRequest();
        duplicateEmailRequest.setFirstname("Anna");
        duplicateEmailRequest.setLastname("Meier");
        duplicateEmailRequest.setEmail("test@mail.com"); // Bereits existierender User
        duplicateEmailRequest.setPassword("anotherPW");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerFailsWhenEmailInvalid() throws Exception {
        validRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void registerFailsWhenPasswordTooShort() throws Exception {
        validRequest.setPassword("12");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerFailsWhenFirstnameMissing() throws Exception {
        validRequest.setFirstname(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void registerFailsWhenLastnameMissing() throws Exception {
        validRequest.setLastname(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void registerFailsWhenBodyEmpty() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }
}
