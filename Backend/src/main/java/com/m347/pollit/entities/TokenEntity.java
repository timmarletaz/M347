package com.m347.pollit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity owner;

    private LocalDateTime created;
    private LocalDateTime expires;

    public TokenEntity(String token, UserEntity owner) {
        this.token = token;
        this.owner = owner;
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusDays(10);
    }
}
