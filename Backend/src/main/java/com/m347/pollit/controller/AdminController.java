package com.m347.pollit.controller;

import com.m347.pollit.entities.Poll;
import com.m347.pollit.repositories.PollRepository;
import com.m347.pollit.responses.PollPreviewResponse;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/admin")
@RolesAllowed("ROLE_ADMIN")
public class AdminController {

    @Autowired
    private PollRepository pollRepository;

    @GetMapping("polls/all")
    public List<PollPreviewResponse> allPolls() {
        List<Poll> polls = pollRepository.findAll();
        List<PollPreviewResponse> responses = new ArrayList<>();
        polls.stream().forEach(e -> {
            responses.add(new PollPreviewResponse(e.getUuid(), e.getTitle(), e.getDescription()));
        });
        return responses;
    }


}
