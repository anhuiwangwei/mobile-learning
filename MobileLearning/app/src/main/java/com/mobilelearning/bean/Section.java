package com.mobilelearning.bean;

import java.io.Serializable;

public class Section implements Serializable {
    private Long id;
    private Long chapterId;
    private String sectionName;
    private String sectionType;
    private String contentUrl;
    private Integer duration;
    private Integer pdfReadTime;
    private Integer isAllowSeek;
    private Integer isStepLearning;
    private Integer isFree;
    private Integer sort;
    private Integer status;
    private Long examId;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    public String getSectionType() { return sectionType; }
    public void setSectionType(String sectionType) { this.sectionType = sectionType; }
    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Integer getPdfReadTime() { return pdfReadTime; }
    public void setPdfReadTime(Integer pdfReadTime) { this.pdfReadTime = pdfReadTime; }
    public Integer getIsAllowSeek() { return isAllowSeek; }
    public void setIsAllowSeek(Integer isAllowSeek) { this.isAllowSeek = isAllowSeek; }
    public Integer getIsStepLearning() { return isStepLearning; }
    public void setIsStepLearning(Integer isStepLearning) { this.isStepLearning = isStepLearning; }
    public Integer getIsFree() { return isFree; }
    public void setIsFree(Integer isFree) { this.isFree = isFree; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
}
