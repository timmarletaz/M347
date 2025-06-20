package com.m347.pollit.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PollPreviewResponse {
    private String uuid;
    private String title;
    private String description;
}
