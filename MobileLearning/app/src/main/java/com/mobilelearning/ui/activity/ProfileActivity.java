package com.mobilelearning.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobilelearning.R;
import com.mobilelearning.api.BaseResponse;
import com.mobilelearning.api.UserApi;
import com.mobilelearning.bean.User;
import com.mobilelearning.utils.RetrofitUtil;
import com.mobilelearning.utils.SpUtil;

import java.lang.reflect.Type;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    
    private TextView tvUsername;
    private TextView tvRealName;
    private TextView tvPhone;
    private TextView tvRole;
    private Button btnEditProfile;
    private Button btnChangePassword;
    private Button btnLogout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        tvUsername = findViewById(R.id.tv_username);
        tvRealName = findViewById(R.id.tv_real_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvRole = findViewById(R.id.tv_role);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
        
        loadUserInfo();
        
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "编辑个人资料功能开发中", Toast.LENGTH_SHORT).show();
        });
        
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "修改密码功能开发中", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            SpUtil.getInstance(this).clear();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void loadUserInfo() {
        UserApi api = RetrofitUtil.create(UserApi.class);
        api.getUserInfo().enqueue(new Callback<BaseResponse<User>>() {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        tvUsername.setText(user.getUsername() != null ? user.getUsername() : "-");
                        tvRealName.setText(user.getRealName() != null ? user.getRealName() : "-");
                        tvPhone.setText(user.getPhone() != null ? user.getPhone() : "-");
                        tvRole.setText(getRoleText(user.getRole()));
                    }
                } else {
                    loadLocalUserInfo();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                loadLocalUserInfo();
            }
        });
    }
    
    private void loadLocalUserInfo() {
        String username = SpUtil.getInstance(this).getString("username");
        String realName = SpUtil.getInstance(this).getString("realName");
        String phone = SpUtil.getInstance(this).getString("phone");
        String role = SpUtil.getInstance(this).getString("role");
        
        tvUsername.setText(username.isEmpty() ? "-" : username);
        tvRealName.setText(realName.isEmpty() ? "-" : realName);
        tvPhone.setText(phone.isEmpty() ? "-" : phone);
        tvRole.setText(getRoleText(role));
    }
    
    private String getRoleText(String role) {
        if ("admin".equals(role)) {
            return "管理员";
        } else if ("teacher".equals(role)) {
            return "教师";
        } else if ("user".equals(role)) {
            return "学生";
        }
        return role != null ? role : "-";
    }
}
