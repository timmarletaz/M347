package com.m347.pollit.responses;

import com.m347.pollit.entities.Element;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ElementSummary {
    private Element element;
    private List<SummaryElement> topAnswers = new ArrayList();
}

