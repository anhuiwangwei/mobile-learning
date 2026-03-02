package com.mobilelearning.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamSubmitRequest {
    private Long recordId;
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String userAnswer;
    }
}
