package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.CourseApi;
import com.mobilelearning.bean.Chapter;
import com.mobilelearning.bean.Section;
import com.mobilelearning.ui.adapter.ChapterExpandableAdapter;
import com.mobilelearning.utils.RetrofitUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {
    
    private TextView tvCourseName;
    private ExpandableListView expandableListView;
    private List<Chapter> chapterList = new ArrayList<>();
    private ChapterExpandableAdapter adapter;
    
    private long courseId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        
        courseId = getIntent().getLongExtra("courseId", 0);
        String courseName = getIntent().getStringExtra("courseName");
        
        tvCourseName = findViewById(R.id.tv_course_name);
        expandableListView = findViewById(R.id.elv_chapters);
        
        if (courseName != null) {
            tvCourseName.setText(courseName);
        }
        
        // 先设置空 adapter
        adapter = new ChapterExpandableAdapter(this, chapterList);
        expandableListView.setAdapter(adapter);
        
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            Chapter chapter = chapterList.get(groupPosition);
            if (chapter.getSections() != null && !chapter.getSections().isEmpty()) {
                Section section = chapter.getSections().get(childPosition);
                startSection(section);
            }
            return true;
        });
        
        loadCourseDetail();
    }
    
    private void startSection(Section section) {
        if ("video".equals(section.getSectionType())) {
            Intent intent = new Intent(this, VideoStudyActivity.class);
            intent.putExtra("sectionId", section.getId());
            intent.putExtra("sectionName", section.getSectionName());
            intent.putExtra("videoUrl", section.getContentUrl());
            intent.putExtra("isAllowSeek", section.getIsAllowSeek() == 1);
            intent.putExtra("isStepLearning", section.getIsStepLearning() == 1);
            startActivity(intent);
        } else if ("pdf".equals(section.getSectionType())) {
            Intent intent = new Intent(this, PdfStudyActivity.class);
            intent.putExtra("sectionId", section.getId());
            intent.putExtra("sectionName", section.getSectionName());
            intent.putExtra("pdfUrl", section.getContentUrl());
            intent.putExtra("pageCount", section.getDuration() != null ? section.getDuration() : 0);
            intent.putExtra("pdfReadTime", section.getPdfReadTime() != null ? section.getPdfReadTime() : 300);
            startActivity(intent);
        } else if ("exam".equals(section.getSectionType())) {
            Intent intent = new Intent(this, ExamActivity.class);
            intent.putExtra("sectionId", section.getId());
            intent.putExtra("examId", section.getExamId());
            intent.putExtra("sectionName", section.getSectionName());
            startActivity(intent);
        } else {
            Toast.makeText(this, "暂不支持该类型", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadCourseDetail() {
        CourseApi api = RetrofitUtil.create(CourseApi.class);
        api.getChapters(courseId).enqueue(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                Log.d("CourseDetail", "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object dataObj = response.body().getData();
                    Log.d("CourseDetail", "Data type: " + (dataObj != null ? dataObj.getClass().getName() : "null"));
                    Log.d("CourseDetail", "Data: " + new Gson().toJson(dataObj));
                    
                    if (dataObj instanceof List) {
                        List<?> dataList = (List<?>) dataObj;
                        Log.d("CourseDetail", "Data list size: " + dataList.size());
                        
                        if (!dataList.isEmpty()) {
                            Gson gson = new Gson();
                            String json = gson.toJson(dataList);
                            Type chapterListType = new com.google.gson.reflect.TypeToken<List<Chapter>>(){}.getType();
                            List<Chapter> chapters = gson.fromJson(json, chapterListType);
                            
                            chapterList.clear();
                            chapterList.addAll(chapters);
                            adapter.notifyDataSetChanged();
                            
                            Log.d("CourseDetail", "chapterList size: " + chapterList.size());
                            for (int i = 0; i < chapterList.size(); i++) {
                                Chapter chapter = chapterList.get(i);
                                int sectionCount = chapter.getSections() != null ? chapter.getSections().size() : 0;
                                Log.d("CourseDetail", "Chapter " + i + ": " + chapter.getChapterName() + ", sections: " + sectionCount);
                                if (chapter.getSections() != null) {
                                    for (int j = 0; j < chapter.getSections().size(); j++) {
                                        Section section = chapter.getSections().get(j);
                                        Log.d("CourseDetail", "  Section " + j + ": " + section.getSectionName() + ", type: " + section.getSectionType());
                                    }
                                }
                            }
                            
                            if (!chapterList.isEmpty()) {
                                expandableListView.expandGroup(0);
                            }
                            
                            int totalSections = 0;
                            for (Chapter chapter : chapterList) {
                                if (chapter.getSections() != null) {
                                    totalSections += chapter.getSections().size();
                                }
                            }
                            
                            Toast.makeText(CourseDetailActivity.this, 
                                "共" + chapterList.size() + "章，" + totalSections + "节", 
                                Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CourseDetailActivity.this, "暂无章节", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("CourseDetail", "Data is not a List: " + dataObj);
                        Toast.makeText(CourseDetailActivity.this, "数据格式错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "未知错误";
                    Log.e("CourseDetail", "Error: " + errorMsg);
                    Toast.makeText(CourseDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                Log.e("CourseDetail", "Network error", t);
                Toast.makeText(CourseDetailActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
