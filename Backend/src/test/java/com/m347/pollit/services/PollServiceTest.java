package com.m347.pollit.services;

import com.m347.pollit.ElementType;
import com.m347.pollit.entities.Element;
import com.m347.pollit.entities.Poll;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.PollRepository;
import com.m347.pollit.requests.AnswerRequest;
import com.m347.pollit.requests.CreatePollRequest;
import com.m347.pollit.requests.ElementRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Component
@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private PollService pollService;

    CreatePollRequest createPollRequest = new CreatePollRequest("Test", "beschreibung", new ArrayList<>());
    UserEntity owner = new UserEntity();


    @Test
    void createPoll() {
        CreatePollRequest createPollRequest = new CreatePollRequest("Test", "beschreibung", Arrays.asList(new ElementRequest("LABEL", "PLACEHOLDER", ElementType.EMAIL, true)));
        UserEntity owner = new UserEntity();
        when(pollRepository.findByUuid(anyString())).thenReturn(Optional.empty());
        Poll poll = pollService.createPoll(createPollRequest, owner);
        assertEquals("Test", poll.getTitle());
        verify(pollRepository, times(1)).save(poll);
    }

    @Test
    void createPollWithoutElementError() {
        assertThrows(CommonException.class, () -> this.pollService.createPoll(createPollRequest, owner));
    }

    @Test
    void createPollWithoutTitleError() {
        createPollRequest.setTitle("");
        createPollRequest.setElements(Arrays.asList(new ElementRequest("LABEL", "PLACEHOLDER", ElementType.EMAIL, true)));
        assertThrows(CommonException.class, () -> this.pollService.createPoll(createPollRequest, owner));
    }

    @Test
    void evaluateAnswers() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), null);
        poll.setElements(List.of(new Element("LABEL", ElementType.DATE, "PLACEHOLDER"), new Element("LABEL", ElementType.EMAIL, "PLACEHOLDER"), new Element("LABEL", ElementType.CHECKBOX, "PLACEHOLDER"), new Element("LABEL", ElementType.SLIDER, "PLACEHOLDER"), new Element("LABEL", ElementType.NUMBER, "PLACEHOLDER")));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList("2025-02-02", "tim@gmail.com", "checked", "5", "5"));
        pollService.evaluateAnswers(answerRequest, poll);
        verify(pollRepository, times(1)).save(poll);
    }

    @Test
    void evaluateAnswersRequiredError() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), Arrays.asList(new Element(1L, "LABEL", ElementType.DATE, "PLACEHOLDER", false, null, null), new Element(2L, "LABEL", ElementType.DATE, "PLACEHOLDER", true, null, null)));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList("asd@asd.com"));
        assertThrows(CommonException.class, () -> pollService.evaluateAnswers(answerRequest, poll));
    }

    @Test
    void evaluateAnswersEmptyListError() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), Arrays.asList(new Element(1L, "LABEL", ElementType.DATE, "PLACEHOLDER", true, null, null)));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList());
        assertThrows(CommonException.class, () -> pollService.evaluateAnswers(answerRequest, poll));
    }

    @Test
    void evaluateDateError() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), Arrays.asList(new Element("LABEL", ElementType.DATE, "PLACEHOLDER")));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList("12-13-2029"));
        assertThrows(CommonException.class, () -> pollService.evaluateAnswers(answerRequest, poll));
    }

    @Test
    void evaluateEmailError() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), Arrays.asList(new Element("LABEL", ElementType.EMAIL, "PLACEHOLDER")));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList("a@a.a"));
        assertThrows(CommonException.class, () -> pollService.evaluateAnswers(answerRequest, poll));
    }

    @Test
    void evaluateCheckboxError() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), Arrays.asList(new Element("LABEL", ElementType.CHECKBOX, "PLACEHOLDER")));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList("1"));
        assertThrows(CommonException.class, () -> pollService.evaluateAnswers(answerRequest, poll));
    }

    @Test
    void evaluateNumberError() {
        Poll poll = new Poll(1L, pollService.generateUniquePollId(), "Tim", "Test", new UserEntity(), Arrays.asList(new Element("LABEL", ElementType.NUMBER, "PLACEHOLDER")));
        AnswerRequest answerRequest = new AnswerRequest(Arrays.asList("1a"));
        assertThrows(CommonException.class, () -> pollService.evaluateAnswers(answerRequest, poll));
    }

    @Test
    void testIdGenerating() {
        assertTrue(this.pollService.generateUniquePollId().matches("[A-Z0-9]{8}"));
    }

}