package com.m347.pollit.services;

import com.m347.pollit.entities.TokenEntity;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.TokenRepository;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.LoginRequest;
import com.m347.pollit.requests.RegisterRequest;
import com.m347.pollit.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserEntity register(RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new CommonException("User mit email " + registerRequest.getEmail() + " existiert bereits");
        }
        UserEntity user = new UserEntity(registerRequest.getFirstname(), registerRequest.getLastname(), registerRequest.getEmail(), encoder.encode(registerRequest.getPassword()));
        return userRepository.save(user);
    }

    public UserEntity login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new CommonException("Kein User mit Email " + loginRequest.getEmail() + " gefunden"));
        if(encoder.matches(loginRequest.getPassword(), user.getPassword())) {
           return user;
        } else {
            throw new CommonException("Falsche Email oder Passwort");
        }
    }

    public TokenEntity generateToken(UserEntity owner) {
        TokenEntity tokenEntity = new TokenEntity(UUID.randomUUID().toString(), owner);
        return this.tokenRepository.save(tokenEntity);
    }

    public UserEntity extractUserFromToken(String token) {
        UserEntity userEntity = this.tokenRepository.findByToken(token).orElseThrow(() -> new CommonException("Ungültiges Token")).getOwner();
        return userEntity;
    }

    public boolean getTokenState(String token) {
        TokenEntity tokenEntity = this.tokenRepository.findByToken(token).orElse(null);
        if(tokenEntity != null) {
            return tokenEntity.getExpires().isAfter(LocalDateTime.now());
        }
        return false;
    }

}
