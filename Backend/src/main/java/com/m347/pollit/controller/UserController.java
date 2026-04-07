package com.m347.pollit.controller;

import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.responses.PollPreviewResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    @GetMapping("polls/all")
    public List<PollPreviewResponse> getAllPolls() {
        try {
            UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getPolls().stream().map(existingPoll -> new PollPreviewResponse(existingPoll.getUuid(), existingPoll.getTitle(), existingPoll.getDescription())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CommonException("Authentifizierung fehlgeschlagen", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping()
    public UserEntity getUser() {
        try {
            return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new CommonException("Authentifizierung fehlgeschlagen", HttpStatus.UNAUTHORIZED);
        }
    }
}
