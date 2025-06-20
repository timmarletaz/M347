package com.m347.pollit.responses;

import com.m347.pollit.entities.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryElement {
    private Answer answer;
    private int count;
}
