package com.m347.pollit.services;

import com.m347.pollit.entities.TokenEntity;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.TokenRepository;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.LoginRequest;
import com.m347.pollit.requests.RegisterRequest;
import com.m347.pollit.requests.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;


    private final TokenRepository tokenRepository;

    private final Clock clock;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository, Clock clock) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.clock = clock;
    }
    //    getestet
    public UserEntity register(RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new CommonException("User mit email " + registerRequest.getEmail() + " existiert bereits");
        }
        UserEntity user = new UserEntity(registerRequest.getFirstname(), registerRequest.getLastname(), registerRequest.getEmail(), encoder.encode(registerRequest.getPassword()));
        return userRepository.save(user);
    }
    //    getestet
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

//    getestet
    public boolean getTokenState(String token) {
            return tokenRepository.findByToken(token)
                    .map(t -> t.getExpires().isAfter(LocalDateTime.now(clock)))
                    .orElse(false);
    }

//    TDD

    public UserEntity updateUserData(int userId, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException("User wurde nicht gefunden"));

        if(!request.getEmail().matches("^[A-Za-z0-9+_.-]{2,}@[A-Za-z0-9.-]{2,}\\.[a-z]{2,}$")){
            throw new CommonException("E-Mail Format ungültig");
        }

        if(request.getFirstName() == null || request.getFirstName().isEmpty()){
            throw new CommonException("Vorname darf nicht leer sein");
        }

        if(request.getLastName() == null || request.getLastName().isEmpty()){
            throw new CommonException("Nachname darf nicht leer sein");
        }

        if(request.getPassword().matches(user.getPassword())) {
            throw new CommonException("Neues Passwort darf nicht gleich wie das alte sein");
        }


        user.setFirstname(request.getFirstName());
        user.setLastname(request.getLastName());
        // BUG: Absichtlich Passwort nicht überschreiben
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());


        return userRepository.save(user);
    }

    public void deleteUser(int userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            // BUG: Anstatt delete(user) wird deleteAll() aufgerufen
            // userRepository.deleteAll();
            userRepository.delete(userOpt.get());
        } else {
            throw new CommonException("User wurde nicht gefunden");
        }
    }


}
