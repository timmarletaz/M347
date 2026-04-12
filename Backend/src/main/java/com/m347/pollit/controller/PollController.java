package com.m347.pollit.controller;

import com.m347.pollit.entities.Answer;
import com.m347.pollit.entities.Poll;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.requests.AnswerRequest;
import com.m347.pollit.requests.CreatePollRequest;
import com.m347.pollit.responses.AdminResponse;
import com.m347.pollit.services.PollService;
import com.m347.pollit.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/polls")
public class PollController {

    @Autowired
    private UserService userService;

    @Autowired
    private PollService pollService;

    @RolesAllowed("ROLE_USER")
    @GetMapping("test")
    public String test() {
        return "Erfolgreich";
    }

    @GetMapping("{id}")
    public Poll getPoll(@PathVariable String id) {
        return this.pollService.getPublicPoll(id);
    }

    @PostMapping("create")
    public Poll createPoll(@RequestBody CreatePollRequest poll) {
        UserEntity user = userService.getUserFromSession();
        Poll savedPoll = this.pollService.createPoll(poll, user);
        return savedPoll;
    }

    @PostMapping("{id}/submit")
    public void submitAnswer(@RequestBody AnswerRequest answerRequest, @PathVariable String id) {
        Poll poll = this.pollService.getPollByUuid(id);
        this.pollService.evaluateAnswers(answerRequest, poll);
    }

    @GetMapping("{id}/admin")
    public AdminResponse getSummary(@PathVariable String id) {
        UserEntity user = userService.getUserFromSession();
        Poll poll = this.pollService.getPollByUuid(id);
        if (!poll.getCreator().equals(user)) {
            throw new CommonException("Nicht berechtigt diese Aktion auszuführen");
        }
        return this.pollService.generateSummary(poll);
    }

    @GetMapping("{id}/all/{elementId}")
    public List<Answer> getAllAnswersForElement(@PathVariable int elementId, @PathVariable String id) {
        UserEntity user = userService.getUserFromSession();
        Poll poll = this.pollService.getPollByUuid(id);
        if (!poll.getCreator().equals(user)) {
            throw new CommonException("Nicht berechtigt diese Aktion auszuführen", HttpStatus.UNAUTHORIZED);
        }
        return this.pollService.getEveryAnswerOfElement(elementId);
    }

    @PostMapping("debug")
    public Object debug(Authentication auth, HttpSession session) {
        return session.getAttribute("SPRING_SECURITY_CONTEXT");
    }

}
