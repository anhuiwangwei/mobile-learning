package com.mobilelearning.api;

import com.mobilelearning.bean.Chapter;
import com.mobilelearning.bean.Course;
import com.mobilelearning.bean.Section;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CourseApi {
    
    @GET("api/course/list")
    Call<BaseResponse<List<Map<String, Object>>>> getCourseList(@Query("categoryId") Long categoryId, @Query("status") Integer status);
    
    @GET("api/course/{id}")
    Call<BaseResponse<Map<String, Object>>> getCourseDetail(@Path("id") Long id);
    
    @GET("api/course/{courseId}/chapters")
    Call<BaseResponse<Object>> getChapters(@Path("courseId") Long courseId);
    
    @GET("api/course/section/{id}")
    Call<BaseResponse<Section>> getSection(@Path("id") Long id);
    
    @GET("api/course/{courseId}/progress")
    Call<BaseResponse<Map<String, Object>>> getCourseProgress(@Path("courseId") Long courseId);
}
