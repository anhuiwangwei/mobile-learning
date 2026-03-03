package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private LinearLayout llCheckBoxOptions;
    private EditText etAnswer;
    private Button btnSubmit;
    private Button btnNext;
    private LinearLayout llOptionContainer;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    
    private List<ExamQuestion> questions = new ArrayList<>();
    private int currentIndex = 0;
    private Long examId;
    private Long recordId;
    private Long sectionId;
    private boolean isMultiple = false;
    
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
        llCheckBoxOptions = findViewById(R.id.ll_checkbox_options);
        etAnswer = findViewById(R.id.et_answer);
        btnSubmit = findViewById(R.id.btn_submit);
        btnNext = findViewById(R.id.btn_next);
        llOptionContainer = findViewById(R.id.ll_option_container);
        
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
        
        rgOptions.removeAllViews();
        llCheckBoxOptions.removeAllViews();
        checkBoxList.clear();
        
        isMultiple = "multiple".equals(question.getQuestionType()) || (question.getIsMultiple() != null && question.getIsMultiple() == 1);
        
        if ("judge".equals(question.getQuestionType())) {
            llOptionContainer.setVisibility(View.VISIBLE);
            rgOptions.setVisibility(View.VISIBLE);
            llCheckBoxOptions.setVisibility(View.GONE);
            etAnswer.setVisibility(View.GONE);
            
            RadioButton rbTrue = new RadioButton(this);
            rbTrue.setText("正确");
            rgOptions.addView(rbTrue);
            
            RadioButton rbFalse = new RadioButton(this);
            rbFalse.setText("错误");
            rgOptions.addView(rbFalse);
        } else {
            llOptionContainer.setVisibility(View.VISIBLE);
            String optionsStr = question.getOptions();
            if (optionsStr != null && !optionsStr.isEmpty()) {
                String[] optionArray = optionsStr.split("\\|");
                
                if (isMultiple) {
                    rgOptions.setVisibility(View.GONE);
                    llCheckBoxOptions.setVisibility(View.VISIBLE);
                    
                    for (int i = 0; i < optionArray.length; i++) {
                        String optText = optionArray[i].trim();
                        if (optText.startsWith(String.valueOf((char)('A' + i)) + ".")) {
                            optText = optText.substring(3).trim();
                        }
                        
                        CheckBox checkBox = new CheckBox(this);
                        checkBox.setText(((char)('A' + i)) + ". " + optText);
                        checkBox.setTag(String.valueOf((char)('A' + i)));
                        llCheckBoxOptions.addView(checkBox);
                        checkBoxList.add(checkBox);
                    }
                } else {
                    rgOptions.setVisibility(View.VISIBLE);
                    llCheckBoxOptions.setVisibility(View.GONE);
                    etAnswer.setVisibility(View.GONE);
                    
                    for (int i = 0; i < optionArray.length; i++) {
                        String optText = optionArray[i].trim();
                        if (optText.startsWith(String.valueOf((char)('A' + i)) + ".")) {
                            optText = optText.substring(3).trim();
                        }
                        
                        RadioButton radioButton = new RadioButton(this);
                        radioButton.setText(((char)('A' + i)) + ". " + optText);
                        radioButton.setTag(String.valueOf((char)('A' + i)));
                        rgOptions.addView(radioButton);
                    }
                }
            }
        }
        
        rgOptions.clearCheck();
        etAnswer.setText("");
    }
    
    private void saveCurrentAnswer() {
        if (currentIndex < questions.size()) {
            ExamQuestion question = questions.get(currentIndex);
            String answer = "";
            
            if ("judge".equals(question.getQuestionType())) {
                int selectedId = rgOptions.getCheckedRadioButtonId();
                if (selectedId == rgOptions.getChildAt(0).getId()) {
                    answer = "true";
                } else if (selectedId == rgOptions.getChildAt(1).getId()) {
                    answer = "false";
                }
            } else if (isMultiple) {
                StringBuilder sb = new StringBuilder();
                for (CheckBox checkBox : checkBoxList) {
                    if (checkBox.isChecked()) {
                        if (sb.length() > 0) sb.append(",");
                        sb.append(checkBox.getTag());
                    }
                }
                answer = sb.toString();
            } else {
                int selectedId = rgOptions.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton rb = findViewById(selectedId);
                    if (rb != null && rb.getTag() != null) {
                        answer = rb.getTag().toString();
                    }
                }
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
