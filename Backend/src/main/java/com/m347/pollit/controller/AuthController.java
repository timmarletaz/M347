package com.m347.pollit.controller;

import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.requests.LoginRequest;
import com.m347.pollit.requests.RegisterRequest;
import com.m347.pollit.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("register")
    public UserEntity register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletRequest request) {
        UserEntity user = userService.register(registerRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession(true)
                .setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );
        return user;
    }

    @PostMapping("login")
    public UserEntity login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.getSession(true)
                    .setAttribute(
                            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            SecurityContextHolder.getContext()
                    );

            return (UserEntity) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            log.info("FAILED LOGIN: {}", e.getMessage());
        }
        throw new CommonException("Falsche Username oder Passwort", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @DeleteMapping("logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return new ResponseEntity<>("Erfolgreich abgemeldet", HttpStatus.OK);
        }
        return new ResponseEntity<>("Keine gültige Session gefunden", HttpStatus.UNAUTHORIZED);
    }

}
