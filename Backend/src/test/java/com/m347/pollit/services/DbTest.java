package com.m347.pollit.services;

import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.repositories.PollRepository;
import com.m347.pollit.repositories.UserRepository;
import com.m347.pollit.requests.RegisterRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Component
@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class DbTest {

    @Autowired
    UserService userService;

    @Autowired
    PollService pollService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PollRepository pollRepository;

    @Test
    void testAtomicity() {

        assertThrows(RuntimeException.class, () ->
                userService.createUserFail(new RegisterRequest("Tim", "Marlétaz", "tim@gmail.com", "1234"))
        );

        assertTrue(userRepository.findByEmail("tim@gmail.com").isEmpty());
    }

    @Test
    void testConsistency() {
        userRepository.deleteAll();
        userRepository.save(new UserEntity("Tim", "Marlétaz", "tim@gmail.com", "1234"));

        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.save(new UserEntity("Tim2", "Marlétaz2", "tim@gmail.com", "12345"))
        );

        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testIsolation() throws InterruptedException {
        AtomicReference<UserEntity> user1 = new AtomicReference<>();
        AtomicReference<UserEntity> user2 = new AtomicReference<>();

        Thread t1 = new Thread(() -> {
            user1.set(this.userService.register(new RegisterRequest("Max", "Mustermann", "max@gmail.com", "1234")));
        });

        Thread t2 = new Thread(() -> {
            user2.set(this.userRepository.findByEmail("max@gmail.com").orElse(null));
        });

        t1.start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // nichts
        }
        t2.start();

        t1.join();
        t2.join();

        assertNotEquals(user1.get(), user2.get());

    }

    @Test
    @Transactional
    void testDurabilityBeforeShutdown() {
        userRepository.deleteAll();
        userRepository.save(new UserEntity("Max", "Mustermann", "max@gmail.com", "1234"));
    }

    @Test
    @Transactional
    void testDurabilityAfterShutdown() {
        UserEntity user = userRepository.findByEmail("max@gmail.com").orElse(null);
        assertNotNull(user);
        assertEquals("Max", user.getFirstname());
    }


}
