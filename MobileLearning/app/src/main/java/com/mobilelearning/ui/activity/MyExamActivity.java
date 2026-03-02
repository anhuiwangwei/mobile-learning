package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.ExamApi;
import com.mobilelearning.bean.ExamRecord;
import com.mobilelearning.ui.adapter.ExamRecordAdapter;
import com.mobilelearning.utils.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyExamActivity extends AppCompatActivity {
    
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ExamRecordAdapter adapter;
    private List<ExamRecord> recordList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exam);
        
        listView = findViewById(R.id.lv_exam_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        tvEmpty = findViewById(R.id.tv_empty);
        
        adapter = new ExamRecordAdapter(this, recordList);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ExamRecord record = recordList.get(position);
            Toast.makeText(this, "查看考试详情：" + record.getId(), Toast.LENGTH_SHORT).show();
        });
        
        swipeRefresh.setOnRefreshListener(this::loadRecords);
        
        loadRecords();
    }
    
    private void loadRecords() {
        ExamApi api = RetrofitUtil.create(ExamApi.class);
        api.getMyExamRecords().enqueue(new Callback<BaseResponse<List<ExamRecord>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<ExamRecord>>> call, Response<BaseResponse<List<ExamRecord>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    recordList.clear();
                    List<ExamRecord> records = response.body().getData();
                    if (records != null) {
                        recordList.addAll(records);
                        adapter.notifyDataSetChanged();
                    }
                    if (records.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(MyExamActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<List<ExamRecord>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MyExamActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }
}
