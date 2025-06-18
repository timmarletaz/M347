package com.m347.pollit.requests;

import com.m347.pollit.entities.Element;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePollRequest {
    private String title;
    private String description;
    private List<ElementRequest> elements = new ArrayList<>();
}
