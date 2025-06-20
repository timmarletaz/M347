package com.m347.pollit.controller;

import com.m347.pollit.entities.TokenEntity;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.requests.LoginRequest;
import com.m347.pollit.requests.RegisterRequest;
import com.m347.pollit.responses.LoginResponse;
import com.m347.pollit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public LoginResponse register(@RequestBody RegisterRequest registerRequest) {
        UserEntity user = userService.register(registerRequest);
        TokenEntity token = userService.generateToken(user);
        return new LoginResponse(token.getToken(),token.getExpires(), user);
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        UserEntity user = this.userService.login(loginRequest);
        TokenEntity token = userService.generateToken(user);
        return new LoginResponse(token.getToken(),token.getExpires(), user);
    }
}
