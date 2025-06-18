package com.m347.pollit.services;

import com.m347.pollit.ElementType;
import com.m347.pollit.entities.Answer;
import com.m347.pollit.entities.Element;
import com.m347.pollit.entities.Poll;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.PollRepository;
import com.m347.pollit.requests.AnswerRequest;
import com.m347.pollit.requests.CreatePollRequest;
import com.m347.pollit.requests.ElementRequest;
import com.m347.pollit.responses.AdminResponse;
import com.m347.pollit.responses.ElementSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    public Poll createPoll(CreatePollRequest createPollRequest, UserEntity owner) {
        Poll poll = new Poll();
        if (createPollRequest.getTitle().trim().isEmpty() || createPollRequest.getElements().isEmpty()) {
            throw new CommonException("Felder müssen ausgefüllt sein");
        }
        poll.setTitle(createPollRequest.getTitle());
        for (ElementRequest elementRequest : createPollRequest.getElements()) {
            Element element = new Element(elementRequest.getLabel(), elementRequest.getType(), elementRequest.getPlaceholder());
            poll.addElement(element);
            element.setPoll(poll);
        }
        poll.setDescription(createPollRequest.getDescription());
        poll.setCreator(owner);
        poll.setUuid(this.generateUniquePollId());
        this.pollRepository.save(poll);
        return poll;
    }

    public Poll getPublicPoll(String uuid) {
        return this.pollRepository.findByUuid(uuid).orElseThrow(() -> new CommonException("Umfrage wurde nicht gefunden"));
    }

    public void evaluateAnswers(AnswerRequest answerRequest, Poll poll) {
        //TODO, bei jeder Antwort checken, ob sie bereits existiert (nicht case sensitive) und dann count erhöhen
        List<Element> elements = poll.getElements();
        if (answerRequest.getValues().size() != elements.size()) {
            throw new CommonException("Bitte alle Fragen beantworten");
        }
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String answer = answerRequest.getValues().get(i);
            if ((answer == null || answer.trim().isEmpty())) {
                if (element.isRequired()) {
                    throw new CommonException("Bitte alle Fragen beantworten");
                }
            } else if (element.getType() == ElementType.DATE) {
                try {
                    LocalDateTime.parse(answer);
                    Answer newAnswer = new Answer(element, answer);
                    element.addAnswer(newAnswer);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                    throw new CommonException("Bitte gültiges Datum eingeben");
                }
            } else if (element.getType() == ElementType.EMAIL) {
                if (answer.matches("^[A-Za-z0-9+_.-]{2,}@[A-Za-z0-9.-]{2,}\\.[a-z]{2,}$")) {
                    this.addAnswer(element,answer);
                } else {
                    throw new CommonException("Ungültige Email-Adresse");
                }
            } else if (element.getType() == ElementType.CHECKBOX){
                if(answer.contains("checked") || answer.contains("unchecked")) {
                    Answer newAnswer = new Answer(element, answer);
                    element.addAnswer(newAnswer);
                } else {
                    throw new CommonException("Bitte alle Fragen beantworten");
                }
            } else if (element.getType() == ElementType.SLIDER || element.getType() == ElementType.NUMBER) {
                try {
                    Integer.parseInt(answer);
                    Answer newAnswer = new Answer(element, answer);
                    element.addAnswer(newAnswer);
                } catch (NumberFormatException e) {
                    throw new CommonException("Bitte alle Fragen beantworten");
                }
            } else {
                Answer newAnswer = new Answer(element, answer);
                element.addAnswer(newAnswer);
            }
        }
        this.pollRepository.save(poll);
    }

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    public String generateUniquePollId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        do {
            for (int i = 0; i < 4; i++) {
                sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
            }
            for (int i = 0; i < 4; i++) {
                sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
            }
        } while (this.pollRepository.findByUuid(sb.toString()).isPresent());
        return sb.toString();
    }

    public Poll getPollByUuid(String uuid) {
        return this.pollRepository.findByUuid(uuid).orElseThrow(() -> new CommonException("Umfrage wurde nicht gefunden"));
    }

    public void addAnswer(Element element, String answer) {
        //TODO überprüfen, ob es ein Match gibt
        Answer newAnswer = new Answer(element, answer);
        element.addAnswer(newAnswer);
    }

    public AdminResponse generateSummary(Poll poll) {
        List<ElementSummary> elementSummaries = new ArrayList<>();
        for(Element element : poll.getElements()) {
            //TODO Smarter machen
        }
        return null;
    }
}
