package com.m347.pollit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m347.pollit.ElementType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @Enumerated(EnumType.STRING)
    private ElementType type;

    @Nullable
    private String placeholder;

    private boolean required = false;

    @JsonIgnore
    @OneToMany(mappedBy = "element", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Answer> answers = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public Element(String label, ElementType type, @Nullable String placeholder) {
        this.label = label;
        this.type = type;
        this.placeholder = placeholder;
    }
}
