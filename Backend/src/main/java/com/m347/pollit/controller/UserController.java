package com.m347.pollit.controller;

import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.responses.PollPreviewResponse;
import com.m347.pollit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("polls/all")
    public List<PollPreviewResponse> getAllPolls() {
        UserEntity user = userService.getUserFromSession();
        return user.getPolls().stream().map(existingPoll -> new PollPreviewResponse(existingPoll.getUuid(), existingPoll.getTitle(), existingPoll.getDescription())).collect(Collectors.toList());
    }

    @GetMapping()
    public UserEntity getUser() {
        try {
            return userService.getUserFromSession();
        } catch (Exception e) {
            throw new CommonException("Authentifizierung fehlgeschlagen", HttpStatus.UNAUTHORIZED);
        }
    }

}
