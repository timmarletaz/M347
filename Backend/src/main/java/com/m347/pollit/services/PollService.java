package com.m347.pollit.services;

import ch.qos.logback.core.util.StringUtil;
import com.m347.pollit.ElementType;
import com.m347.pollit.entities.Answer;
import com.m347.pollit.entities.Element;
import com.m347.pollit.entities.Poll;
import com.m347.pollit.entities.UserEntity;
import com.m347.pollit.exceptions.CommonException;
import com.m347.pollit.repositories.ElementRepository;
import com.m347.pollit.repositories.PollRepository;
import com.m347.pollit.requests.*;
import com.m347.pollit.responses.AdminResponse;
import com.m347.pollit.responses.ElementSummary;
import com.m347.pollit.responses.PollDto;
import com.m347.pollit.responses.SummaryElement;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.module.FindException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ElementRepository elementRepository;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    @Transactional
    public Poll createPoll(CreatePollRequest createPollRequest, UserEntity owner) {
        Poll poll = new Poll();
        if (createPollRequest.getTitle().trim().isEmpty() || createPollRequest.getElements().isEmpty()) {
            throw new CommonException("Felder müssen ausgefüllt sein");
        }
        poll.setTitle(createPollRequest.getTitle());
        for (ElementRequest elementRequest : createPollRequest.getElements()) {
            Element element = new Element(elementRequest.getLabel(), elementRequest.getType(), elementRequest.getPlaceholder());
            poll.addElement(element);
            element.setRequired(elementRequest.isRequired());
            element.setPoll(poll);
        }

        poll.setDescription(createPollRequest.getDescription());
        poll.setCreator(owner);
        poll.setUuid(this.generateUniquePollId());
        this.pollRepository.save(poll);
        return poll;
    }

    @Transactional
    public PollDto getPublicPoll(String uuid) {
        Poll poll = this.pollRepository.findByUuid(uuid).orElseThrow(() -> new CommonException("Umfrage wurde nicht gefunden"));
        List<Element> elements = poll.getElements().stream().filter(Element::isActive).toList();
        if(elements.isEmpty()) {
            throw new CommonException("Umfrage beinhaltet keine Elemente", HttpStatus.NOT_FOUND);
        }
        return new PollDto(poll.getId(), poll.getTitle(), poll.getUuid(), poll.getDescription(), poll.getCreator(), elements);
    }

    @Transactional
    public void evaluateAnswers(AnswerRequest answerRequest, Poll poll) {
        List<Element> elements = poll.getElements();

        if (answerRequest.getValues().size() != elements.size()) {
            throw new CommonException("Bitte alle Fragen beantworten", HttpStatus.NOT_ACCEPTABLE);
        }

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String answer = answerRequest.getValues().get(i);

            if (answer == null || answer.trim().isEmpty()) {
                if (element.isRequired()) {
                    throw new CommonException("Bitte alle Fragen beantworten");
                }
                continue;
            }

            ElementType type = element.getType();

            switch (type) {
                case DATE:
                    try {
                        LocalDate.parse(answer);
                        addAnswer(element, answer);
                    } catch (DateTimeParseException e) {
                        throw new CommonException("Bitte gültiges Datum eingeben");
                    }
                    break;

                case EMAIL:
                    if (answer.matches("^[A-Za-z0-9+_.-]{2,}@[A-Za-z0-9.-]{2,}\\.[a-z]{2,}$")) {
                        addAnswer(element, answer);
                    } else {
                        throw new CommonException("Ungültige Email-Adresse");
                    }
                    break;

                case CHECKBOX:
                    if (answer.contains("checked") || answer.contains("unchecked")) {
                        addAnswer(element, answer);
                    } else {
                        throw new CommonException("Bitte alle Fragen beantworten");
                    }
                    break;

                case SLIDER:
                case NUMBER:
                    try {
                        Float.parseFloat(answer);
                        addAnswer(element, answer);
                    } catch (NumberFormatException e) {
                        throw new CommonException("Bitte eine gültige Zahl eingeben");
                    }
                    break;

                default:
                    addAnswer(element, answer);
                    break;
            }
        }
        this.pollRepository.save(poll);
    }

    public AdminResponse editPoll(String uuid, NewElementRequest newElementRequest) {
        Poll poll =  this.getPollByUuid(uuid);
        if(newElementRequest.getNewElements().isEmpty()) {
            return this.generateSummary(poll);
        }
        UserEntity user = this.userService.getUserFromSession();
        if(!poll.getCreator().equals(user) && !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new CommonException("Nicht berechtigt, diese Aktion auszuführen", HttpStatus.UNAUTHORIZED);
        }
        for(ElementRequest newElement : newElementRequest.getNewElements()) {
            if(newElement.getElementId() != null) {
                int index = poll.getElements().indexOf(poll.getElements().stream().filter(item -> item.getId().equals(newElement.getElementId())).findFirst().orElseThrow(() -> new CommonException("Element wurde nicht gefunden", HttpStatus.NOT_FOUND)));
                poll.addElement(new Element(newElement.getLabel(), newElement.getType(), newElement.getPlaceholder()), index);
                pollRepository.save(poll);
            } else {
                poll.addElement(new Element(newElement.getLabel(), newElement.getType(), newElement.getPlaceholder()));
                pollRepository.save(poll);
            }
        }

        return this.generateSummary(poll);
    }

    public AdminResponse deleteElement(String uuid, Long id) {
        Poll poll = this.pollRepository.findByUuid(uuid).orElseThrow(() -> new CommonException("Poll wurde nicht gefunden"));
        UserEntity user = this.userService.getUserFromSession();
        if(!poll.getCreator().equals(user) && !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new CommonException("Nich berechtigt, diese Aktion auszuführen", HttpStatus.UNAUTHORIZED);
        }
        Element element = poll.getElements().stream().filter(item -> item.getId().equals(id)).findFirst().orElseThrow(() -> new CommonException("Element wurde nicht gefunden", HttpStatus.NOT_FOUND));
        poll.removeElement(element);
        pollRepository.save(poll);
        return this.generateSummary(poll);
    }

    public void deletePoll(String uuid) {
        Poll poll = this.pollRepository.findByUuid(uuid).orElseThrow(() -> new CommonException("Poll wurde nicht gefunden", HttpStatus.NOT_FOUND));
        UserEntity user = this.userService.getUserFromSession();
        if(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || poll.getCreator().equals(user)) {
            pollRepository.delete(poll);
        }
    }

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

    public Element addAnswer(Element element, String answer) {
        for (Answer existingAnswer : element.getAnswers()) {
            if (existingAnswer.getValue().equalsIgnoreCase(answer)) {
                existingAnswer.increaseCount();
                return element;
            }
        }

        element.addAnswer(new Answer(element, answer));
        return element;
    }

    public AdminResponse generateSummary(Poll poll) {
        List<ElementSummary> elementSummaries = new ArrayList<>();

        for (Element element : poll.getElements()) {
            List<SummaryElement> topAnswers = element.getAnswers().stream()
                    .sorted(Comparator.comparingInt(Answer::getCount).reversed())
                    .limit(10)
                    .map(answer -> new SummaryElement(answer, answer.getCount()))
                    .collect(Collectors.toList());

            ElementSummary summary = new ElementSummary(element, element.isActive(), topAnswers);
            elementSummaries.add(summary);
        }

        return new AdminResponse(elementSummaries, poll.getCreator().getFirstname() + " " + poll.getCreator().getLastname());
    }

    @Transactional
    public List<Answer> getEveryAnswerOfElement(int id) {
        Element element = this.elementRepository.findById(id).orElseThrow(() -> new CommonException("Element nicht gefunden"));
        return element.getAnswers().stream().sorted(Comparator.comparingInt(Answer::getCount).reversed()).collect(Collectors.toList());
    }
}
