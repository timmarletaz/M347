package com.m347.pollit.services;

import com.m347.pollit.entities.TokenEntity;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.TokenRepository;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.LoginRequest;
import com.m347.pollit.requests.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    private UserService userService;

    private final UserEntity user = new UserEntity("Max", "Müller", "test@mail.com", "pw");

    @BeforeEach
    void setup() {
        Clock fixedClock = Clock.fixed(Instant.parse("2025-08-19T10:00:00Z"), ZoneId.of("UTC"));
        userService = new UserService(userRepository, tokenRepository, fixedClock);
    }

    @Test
    void tokenValidWhenExpiresInFuture() {
        TokenEntity token = new TokenEntity("abc", user);
        token.setExpires(LocalDateTime.of(2025, 8, 19, 10, 1));
        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertTrue(userService.getTokenState("abc"));
    }

    @Test
    void tokenInvalidWhenAlreadyExpired() {
        TokenEntity token = new TokenEntity("abc", user);
        token.setExpires(LocalDateTime.of(2025, 8, 19, 9, 0)); // eine Stunde früher
        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertFalse(userService.getTokenState("abc"));
    }

    @Test
    void tokenInvalidWhenExpiresExactlyNow() {
        TokenEntity token = new TokenEntity("abc", user);
        token.setExpires(LocalDateTime.of(2025, 8, 19, 10, 0)); // exakt fixedClock
        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertFalse(userService.getTokenState("abc"));
    }

    @Test
    void tokenInvalidWhenTokenNotFound() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertFalse(userService.getTokenState("missing"));
    }

    @Test
    void registerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("Max", "Müller", "test@mail.com", "pw");

        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity user = userService.register(registerRequest);

        assertNotNull(user);
        assertEquals("Max", user.getFirstname());
        assertEquals("Müller", user.getLastname());
        assertEquals("test@mail.com", user.getEmail());
        assertNotEquals("pw", user.getPassword());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void registerFail() {
        RegisterRequest request = new RegisterRequest("Max", "Müller", "test@mail.com", "pass123");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(new UserEntity()));

        CommonException exception = assertThrows(CommonException.class,
                () -> userService.register(request));

        assertEquals("User mit email test@mail.com existiert bereits", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginSuccess() {
        String rawPassword = "pw";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        UserEntity userWithHashedPassword = new UserEntity("Max", "Müller", "test@mail.com", encodedPassword);

        LoginRequest request = new LoginRequest("test@mail.com", rawPassword);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userWithHashedPassword));

        UserEntity result = userService.login(request);

        assertNotNull(result);
        assertEquals("Max", result.getFirstname());
        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void loginFailWithWrongPassword() {
        String correctPassword = "pw";
        String encodedPassword = new BCryptPasswordEncoder().encode(correctPassword);

        UserEntity userWithHashedPassword = new UserEntity("Max", "Müller", "test@mail.com", encodedPassword);

        LoginRequest request = new LoginRequest("test@mail.com", "wrongPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userWithHashedPassword));

        CommonException exception = assertThrows(CommonException.class,
                () -> userService.login(request));

        assertEquals("Falsche Email oder Passwort", exception.getMessage());
    }

//    TDD
    @Test
    void updateUserDataSuccess() {
        // TODO
    }
}