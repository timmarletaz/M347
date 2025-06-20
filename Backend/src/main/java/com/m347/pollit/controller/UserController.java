package com.m347.pollit.controller;

import com.m347.pollit.entities.Poll;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.responses.PollPreviewResponse;
import com.m347.pollit.services.PollService;
import com.m347.pollit.services.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PollService pollService;

    @GetMapping("polls/all")
    public List<PollPreviewResponse> getAllPolls(@RequestHeader String token) {
        if(this.userService.getTokenState(token)) {
            UserEntity user = this.userService.extractUserFromToken(token);
            return user.getPolls().stream().map(existingPoll -> new PollPreviewResponse(existingPoll.getUuid(), existingPoll.getTitle(), existingPoll.getDescription())).collect(Collectors.toList());
        }
        throw new CommonException("Ungültiges Token");
    }

    @GetMapping()
    public UserEntity getUser(@RequestHeader String token) {
        if(this.userService.getTokenState(token)) {
            UserEntity user = this.userService.extractUserFromToken(token);
            return user;
        }
        throw new CommonException("Ungültiges Token");
    }
}
