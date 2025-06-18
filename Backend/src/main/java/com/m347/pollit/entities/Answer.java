package com.m347.pollit.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="element_id")
    private Element element;

    private int count = 1;

    private String value;

    public void increaseCount() {
        count++;
    }

    public Answer(Element element, String value) {
        this.element = element;
        this.value = value;
    }
}
