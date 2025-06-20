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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/polls")
public class PollController {

    @Autowired
    private UserService userService;

    @Autowired
    private PollService pollService;

    @GetMapping("{id}")
    public Poll getPoll(@PathVariable String id) {
        return this.pollService.getPublicPoll(id);
    }

    @PostMapping("create")
    public Poll createPoll(@RequestBody CreatePollRequest poll, @RequestHeader String token) {
        if(this.userService.getTokenState(token)){
            UserEntity user = this.userService.extractUserFromToken(token);
            Poll savedPoll = this.pollService.createPoll(poll, user);
            return savedPoll;
        }
        throw new CommonException("Ungültiges Token");
    }

    @PostMapping("{id}/submit")
    public void submitAnswer(@RequestBody AnswerRequest answerRequest, @PathVariable String id) {
        Poll poll = this.pollService.getPollByUuid(id);
        this.pollService.evaluateAnswers(answerRequest, poll);
    }

    @GetMapping("{id}/admin")
    public AdminResponse getSummary(@PathVariable String id, @RequestHeader String token) {
        if(this.userService.getTokenState(token)){
            UserEntity user = this.userService.extractUserFromToken(token);
            Poll poll = this.pollService.getPollByUuid(id);
            if(!poll.getCreator().equals(user)){
                throw new CommonException("Nicht berechtigt diese Aktion auszuführen");
            }
            return this.pollService.generateSummary(poll);
        }
        throw new CommonException("Ungültiges Token");
    }

    @GetMapping("{id}/all/{elementId}")
    public List<Answer> getAllAnswersForElement(@PathVariable int elementId, @PathVariable String id, @RequestHeader String token) {
        if(this.userService.getTokenState(token)) {
            UserEntity user = this.userService.extractUserFromToken(token);
            Poll poll = this.pollService.getPollByUuid(id);
            if (!poll.getCreator().equals(user)) {
                throw new CommonException("Nicht berechtigt diese Aktion auszuführen");
            }
            return this.pollService.getEveryAnswerOfElement(elementId);
        }
        throw new CommonException("Ungültiges Token");
    }

}
