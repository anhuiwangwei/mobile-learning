package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.CourseApi;
import com.mobilelearning.bean.Course;
import com.mobilelearning.ui.adapter.CourseAdapter;
import com.mobilelearning.utils.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseListActivity extends AppCompatActivity {
    
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;
    private CourseAdapter adapter;
    private List<Course> courseList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        
        listView = findViewById(R.id.lv_course_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        
        adapter = new CourseAdapter(this, courseList);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Course course = courseList.get(position);
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.getId());
            intent.putExtra("courseName", course.getCourseName());
            startActivity(intent);
        });
        
        swipeRefresh.setOnRefreshListener(this::loadCourses);
        
        loadCourses();
    }
    
    private void loadCourses() {
        CourseApi api = RetrofitUtil.create(CourseApi.class);
        api.getCourseList(null, 1).enqueue(new Callback<BaseResponse<List<Map<String, Object>>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Map<String, Object>>>> call, Response<BaseResponse<List<Map<String, Object>>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    courseList.clear();
                    List<Map<String, Object>> dataList = response.body().getData();
                    if (dataList != null) {
                        for (Map<String, Object> item : dataList) {
                            Course course = new Course();
                            Map<String, Object> courseMap = (Map<String, Object>) item.get("course");
                            if (courseMap != null) {
                                course.setId(((Number) courseMap.get("id")).longValue());
                                course.setCourseName((String) courseMap.get("courseName"));
                                course.setCourseDesc((String) courseMap.get("courseDesc"));
                                course.setCoverImage((String) courseMap.get("coverImage"));
                                course.setDifficulty((Integer) courseMap.get("difficulty"));
                                course.setDuration((Integer) courseMap.get("duration"));
                                course.setPageTurnTime((Integer) courseMap.get("pageTurnTime"));
                                course.setIsOrderLearning((Integer) courseMap.get("isOrderLearning"));
                                course.setStatus((Integer) courseMap.get("status"));
                                course.setViewCount((Integer) courseMap.get("viewCount"));
                            }
                            course.setTeacherName((String) item.get("teacherName"));
                            course.setProgress((Map<String, Object>) item.get("progress"));
                            courseList.add(course);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(CourseListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<List<Map<String, Object>>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(CourseListActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }
}
