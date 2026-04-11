package com.m347.pollit.services;

import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.RegisterRequest;
import com.m347.pollit.requests.UpdateUserRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final Clock clock;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, Clock clock, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clock = clock;
        this.passwordEncoder = passwordEncoder;
    }

    //    getestet
    @Transactional
    public UserEntity getUserFromSession() {
        UserEntity sessionUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = (UserEntity) loadUserByUsername(sessionUser.getUsername());
        if (user != null) {
            return user;
        }
        throw new CommonException("Ungültige Session", HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public UserEntity register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new CommonException("User mit email " + registerRequest.getEmail() + " existiert bereits");
        }
        UserEntity user = new UserEntity(registerRequest.getFirstname(), registerRequest.getLastname(), registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()));
        return userRepository.save(user);
    }

    //  TDD
    @Transactional
    public UserEntity updateUserData(int userId, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException("User wurde nicht gefunden"));

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]{2,}@[A-Za-z0-9.-]{2,}\\.[a-z]{2,}$")) {
            throw new CommonException("E-Mail Format ungültig");
        }

        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new CommonException("Vorname darf nicht leer sein");
        }

        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            throw new CommonException("Nachname darf nicht leer sein");
        }

        if (request.getPassword().matches(user.getPassword())) {
            throw new CommonException("Neues Passwort darf nicht gleich wie das alte sein");
        }


        user.setFirstname(request.getFirstName());
        user.setLastname(request.getLastName());
        // BUG: Absichtlich Passwort nicht überschreiben
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());


        return userRepository.save(user);
    }

    @Transactional
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

    @Transactional
    public void createUserFail(RegisterRequest registerRequest) {
        UserEntity user = new UserEntity(registerRequest.getFirstname(), registerRequest.getLastname(), registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
        throw new RuntimeException("Transaction tests");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
