package com.m347.pollit.requests;

import com.m347.pollit.ElementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ElementRequest {
    private String label;
    private String placeholder;
    private ElementType type;
    private boolean required = false;
}
