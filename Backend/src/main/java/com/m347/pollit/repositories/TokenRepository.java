package com.m347.pollit.repositories;

import ch.qos.logback.core.subst.Token;
import com.m347.pollit.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {
    Optional<TokenEntity> findByToken(String token);
}
