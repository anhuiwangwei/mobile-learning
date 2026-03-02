package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.ExamApi;
import com.mobilelearning.bean.ExamPaper;
import com.mobilelearning.bean.ExamQuestion;
import com.mobilelearning.utils.RetrofitUtil;
import com.mobilelearning.utils.SpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamActivity extends AppCompatActivity {
    
    private TextView tvQuestionText;
    private RadioGroup rgOptions;
    private RadioButton rbOptionA;
    private RadioButton rbOptionB;
    private RadioButton rbOptionC;
    private RadioButton rbOptionD;
    private EditText etAnswer;
    private Button btnSubmit;
    private Button btnNext;
    
    private List<ExamQuestion> questions = new ArrayList<>();
    private int currentIndex = 0;
    private Long examId;
    private Long recordId;
    private Long sectionId;
    
    private Map<Long, String> answers = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        
        examId = getIntent().getLongExtra("examId", 0);
        sectionId = getIntent().getLongExtra("sectionId", 0);
        
        initViews();
        startExam();
    }
    
    private void initViews() {
        tvQuestionText = findViewById(R.id.tv_question_text);
        rgOptions = findViewById(R.id.rg_options);
        rbOptionA = findViewById(R.id.rb_option_a);
        rbOptionB = findViewById(R.id.rb_option_b);
        rbOptionC = findViewById(R.id.rb_option_c);
        rbOptionD = findViewById(R.id.rb_option_d);
        etAnswer = findViewById(R.id.et_answer);
        btnSubmit = findViewById(R.id.btn_submit);
        btnNext = findViewById(R.id.btn_next);
        
        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            }
        });
        
        btnSubmit.setOnClickListener(v -> {
            saveCurrentAnswer();
            submitExam();
        });
    }
    
    private void startExam() {
        ExamApi api = RetrofitUtil.create(ExamApi.class);
        api.startExam(examId, null, null).enqueue(new Callback<BaseResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<BaseResponse<Map<String, Object>>> call, Response<BaseResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Map<String, Object> data = response.body().getData();
                    if (data.containsKey("recordId")) {
                        recordId = ((Number) data.get("recordId")).longValue();
                    }
                    Object questionsObj = data.get("questions");
                    if (questionsObj != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ExamQuestion>>(){}.getType();
                        questions = gson.fromJson(gson.toJson(questionsObj), listType);
                        
                        if (!questions.isEmpty()) {
                            showQuestion(0);
                        }
                    }
                } else {
                    Toast.makeText(ExamActivity.this, response.body() != null ? response.body().getMessage() : "开始考试失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ExamActivity.this, "加载试卷失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showQuestion(int index) {
        if (index >= questions.size()) return;
        
        ExamQuestion question = questions.get(index);
        String questionText = (index + 1) + ". " + question.getQuestionContent();
        tvQuestionText.setText(questionText);
        
        if ("judge".equals(question.getQuestionType())) {
            rgOptions.setVisibility(View.VISIBLE);
            etAnswer.setVisibility(View.GONE);
            rbOptionA.setText("正确");
            rbOptionB.setText("错误");
            rbOptionC.setVisibility(View.GONE);
            rbOptionD.setVisibility(View.GONE);
        } else if ("single".equals(question.getQuestionType())) {
            rgOptions.setVisibility(View.VISIBLE);
            etAnswer.setVisibility(View.GONE);
            rbOptionC.setVisibility(View.VISIBLE);
            rbOptionD.setVisibility(View.VISIBLE);
            String[] optionArray = question.getOptions().split("\\|");
            if (optionArray.length >= 4) {
                rbOptionA.setText("A. " + optionArray[0]);
                rbOptionB.setText("B. " + optionArray[1]);
                rbOptionC.setText("C. " + optionArray[2]);
                rbOptionD.setText("D. " + optionArray[3]);
            }
        } else {
            rgOptions.setVisibility(View.GONE);
            etAnswer.setVisibility(View.VISIBLE);
        }
        rgOptions.clearCheck();
        etAnswer.setText("");
    }
    
    private void saveCurrentAnswer() {
        if (currentIndex < questions.size()) {
            ExamQuestion question = questions.get(currentIndex);
            String answer = "";
            
            if (rgOptions.getVisibility() == View.VISIBLE) {
                int selectedId = rgOptions.getCheckedRadioButtonId();
                if (selectedId == R.id.rb_option_a) {
                    answer = "A";
                } else if (selectedId == R.id.rb_option_b) {
                    answer = "B";
                } else if (selectedId == R.id.rb_option_c) {
                    answer = "C";
                } else if (selectedId == R.id.rb_option_d) {
                    answer = "D";
                }
            } else {
                answer = etAnswer.getText().toString().trim();
            }
            
            if (!answer.isEmpty()) {
                answers.put(question.getId(), answer);
                question.setUserAnswer(answer);
            }
        }
    }
    
    private void submitExam() {
        List<ExamApi.ExamSubmitRequest.AnswerItem> answerItems = new ArrayList<>();
        for (ExamQuestion q : questions) {
            if (q.getUserAnswer() != null) {
                answerItems.add(new ExamApi.ExamSubmitRequest.AnswerItem(q.getId(), q.getUserAnswer()));
            }
        }
        
        ExamApi api = RetrofitUtil.create(ExamApi.class);
        ExamApi.ExamSubmitRequest request = new ExamApi.ExamSubmitRequest(recordId, answerItems);
        api.submitExam(request).enqueue(new Callback<BaseResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<BaseResponse<Map<String, Object>>> call, Response<BaseResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ExamActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(ExamActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
