package com.m347.pollit.configs;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Provider {


    @Value("${security.pepper}")
    private String pepper;


    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);
        return new PasswordEncoder() {
            @Override
            public @Nullable String encode(@Nullable CharSequence rawPassword) {
                return bcrypt.encode(rawPassword.toString() +  pepper);
            }

            @Override
            public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
                return bcrypt.matches(rawPassword.toString() + pepper, encodedPassword);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }
}
