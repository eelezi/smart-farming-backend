package com.timmk22.smartfarming.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryAiTipsResponse {

    private String summary;
    private List<String> tips;
    private List<String> instructions;
    private String cultivationAdvice;
}

