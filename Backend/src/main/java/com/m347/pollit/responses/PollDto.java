package com.m347.pollit.responses;

import com.m347.pollit.entities.Element;
import com.m347.pollit.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PollDto {
    private Long id;
    private String title;
    private String uuid;
    private String description;
    private UserEntity creator;
    private List<Element> elements;
}
