package com.mobilelearning.bean;

import java.io.Serializable;
import java.util.List;

public class ExamPaper implements Serializable {
    private Long id;
    private String paperName;
    private Integer totalScore;
    private Integer passScore;
    private Integer duration;
    private Integer questionCount;
    private Integer status;
    private List<ExamQuestion> questions;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaperName() { return paperName; }
    public void setPaperName(String paperName) { this.paperName = paperName; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public Integer getPassScore() { return passScore; }
    public void setPassScore(Integer passScore) { this.passScore = passScore; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<ExamQuestion> getQuestions() { return questions; }
    public void setQuestions(List<ExamQuestion> questions) { this.questions = questions; }
}
