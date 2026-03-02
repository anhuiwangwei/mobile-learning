package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.ExamApi;
import com.mobilelearning.bean.ExamPaper;
import com.mobilelearning.ui.adapter.ExamListAdapter;
import com.mobilelearning.utils.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamListActivity extends AppCompatActivity {
    
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ExamListAdapter adapter;
    private List<ExamPaper> examList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        
        listView = findViewById(R.id.lv_exam_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        tvEmpty = findViewById(R.id.tv_empty);
        
        adapter = new ExamListAdapter(this, examList);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ExamPaper exam = examList.get(position);
            Intent intent = new Intent(this, ExamActivity.class);
            intent.putExtra("examId", exam.getId());
            intent.putExtra("examName", exam.getPaperName());
            startActivity(intent);
        });
        
        swipeRefresh.setOnRefreshListener(this::loadExams);
        
        loadExams();
    }
    
    private void loadExams() {
        ExamApi api = RetrofitUtil.create(ExamApi.class);
        api.getExamList().enqueue(new Callback<BaseResponse<List<ExamPaper>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<ExamPaper>>> call, Response<BaseResponse<List<ExamPaper>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    examList.clear();
                    List<ExamPaper> exams = response.body().getData();
                    if (exams != null) {
                        examList.addAll(exams);
                        adapter.notifyDataSetChanged();
                    }
                    if (exams.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ExamListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<List<ExamPaper>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ExamListActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadExams();
    }
}
