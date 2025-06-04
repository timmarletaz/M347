package com.m347.pollit;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany
    @JoinColumn(name = "answer_id")
    private List<Answer> answers;

}
