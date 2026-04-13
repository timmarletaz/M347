package com.m347.pollit.requests;

import com.m347.pollit.entities.Element;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Size(min = 3, max = 100, message = "Name muss zwischen 3 und 100 Zeichen lang sein")
    private String title;

    private String description;

    @Size(min = 1, max = 50, message = "Poll muss zwischen 1-50 Elemente beinhalten")
    private List<ElementRequest> elements = new ArrayList<>();
}
