package com.m347.pollit.repositories;

import com.m347.pollit.entities.Poll;
import com.m347.pollit.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Integer> {
    Optional<Poll> findByCreator(UserEntity creator);
    Optional<Poll> findByUuid(String uuid);
}
