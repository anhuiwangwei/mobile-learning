package com.mobilelearning.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LearningApi {
    
    @POST("api/learning/video/progress")
    Call<BaseResponse<Void>> updateVideoProgress(
        @Query("sectionId") Long sectionId,
        @Query("progress") Integer progress,
        @Query("watchTime") Integer watchTime
    );
    
    @POST("api/learning/pdf/progress")
    Call<BaseResponse<Void>> updatePdfProgress(
        @Query("sectionId") Long sectionId,
        @Query("currentPage") Integer currentPage,
        @Query("isStart") Boolean isStart
    );
    
    @GET("api/learning/section/{sectionId}")
    Call<BaseResponse<Map<String, Object>>> getSectionStatus(@Path("sectionId") Long sectionId);
}
