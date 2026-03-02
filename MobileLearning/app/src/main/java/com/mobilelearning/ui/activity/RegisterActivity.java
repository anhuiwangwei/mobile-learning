package com.mobilelearning.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.UserApi;
import com.mobilelearning.utils.RetrofitUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etPhone;
    private EditText etName;
    private EditText etPassword;
    private Button btnRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        etPhone = findViewById(R.id.et_phone);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        
        btnRegister.setOnClickListener(v -> register());
    }
    
    private void register() {
        String phone = etPhone.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (phone.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        UserApi api = RetrofitUtil.create(UserApi.class);
        api.register(new UserApi.RegisterRequest(phone, name, password)).enqueue(new Callback<BaseResponse<UserApi.LoginResult>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserApi.LoginResult>> call, Response<BaseResponse<UserApi.LoginResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, response.body() != null ? response.body().getMessage() : "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<UserApi.LoginResult>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
