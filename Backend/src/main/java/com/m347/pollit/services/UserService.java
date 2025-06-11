package com.m347.pollit.services;

import com.m347.pollit.entities.TokenEntity;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.TokenRepository;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.RegisterRequest;
import com.m347.pollit.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public String generateToken(UserEntity owner) {
        TokenEntity tokenEntity = new TokenEntity(UUID.randomUUID().toString(), owner);
        return this.tokenRepository.save(tokenEntity).getToken();
    }
}
