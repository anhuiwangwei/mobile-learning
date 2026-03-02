package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobilelearning.MainApplication;
import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.UserApi;
import com.mobilelearning.utils.RetrofitUtil;
import com.mobilelearning.utils.SpUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        String token = SpUtil.getInstance(this).getString("token");
        if (!token.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        initViews();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        
        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
    
    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        UserApi api = RetrofitUtil.create(UserApi.class);
        api.login(new UserApi.LoginRequest(username, password)).enqueue(new Callback<BaseResponse<UserApi.LoginResult>>() {
            @Override
            public void onResponse(Call<BaseResponse<UserApi.LoginResult>> call, Response<BaseResponse<UserApi.LoginResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserApi.LoginResult result = response.body().getData();
                    SpUtil.getInstance(LoginActivity.this).putString("token", result.getToken());
                    SpUtil.getInstance(LoginActivity.this).putString("username", result.getUsername());
                    SpUtil.getInstance(LoginActivity.this).putString("realName", result.getRealName());
                    SpUtil.getInstance(LoginActivity.this).putString("phone", result.getUsername());
                    SpUtil.getInstance(LoginActivity.this).putString("role", result.getRole());
                    RetrofitUtil.setToken(result.getToken());
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.body() != null ? response.body().getMessage() : "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<UserApi.LoginResult>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
