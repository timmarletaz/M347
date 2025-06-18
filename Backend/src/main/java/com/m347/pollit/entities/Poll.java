package com.m347.pollit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserEntity creator;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "poll")
    private List<Element> elements = new ArrayList<>();

    public void addElement(Element element) {
        elements.add(element);
    }

    public Poll (String uuid, String owner, String title, String description, UserEntity creator) {
        this.uuid = uuid;
        this.title = title;
        this.description = description;
        this.creator = creator;
    }

}
