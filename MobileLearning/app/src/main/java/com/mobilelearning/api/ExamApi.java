package com.mobilelearning.api;

import com.mobilelearning.bean.ExamPaper;
import com.mobilelearning.bean.ExamQuestion;
import com.mobilelearning.bean.ExamRecord;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExamApi {
    
    @GET("api/exam/list")
    Call<BaseResponse<List<ExamPaper>>> getExamList();
    
    @GET("api/exam/{id}")
    Call<BaseResponse<Map<String, Object>>> getExamDetail(@Path("id") Long id);
    
    @POST("api/exam/start")
    Call<BaseResponse<Map<String, Object>>> startExam(
        @Query("examId") Long examId,
        @Query("courseId") Long courseId,
        @Query("chapterId") Long chapterId
    );
    
    @POST("api/exam/submit")
    Call<BaseResponse<Map<String, Object>>> submitExam(@Body ExamSubmitRequest request);
    
    @GET("api/exam/record/{recordId}")
    Call<BaseResponse<Map<String, Object>>> getExamRecord(@Path("recordId") Long recordId);
    
    @GET("api/exam/records")
    Call<BaseResponse<List<ExamRecord>>> getMyExamRecords();
    
    @GET("api/exam/chapter/{chapterId}")
    Call<BaseResponse<List<ExamPaper>>> getChapterExams(@Path("chapterId") Long chapterId);
    
    public class ExamSubmitRequest {
        private Long recordId;
        private List<AnswerItem> answers;
        
        public ExamSubmitRequest(Long recordId, List<AnswerItem> answers) {
            this.recordId = recordId;
            this.answers = answers;
        }
        
        public static class AnswerItem {
            private Long questionId;
            private String userAnswer;
            
            public AnswerItem(Long questionId, String userAnswer) {
                this.questionId = questionId;
                this.userAnswer = userAnswer;
            }
        }
    }
}
