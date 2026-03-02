package com.mobilelearning.api;

import com.mobilelearning.bean.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {
    
    @POST("api/auth/login")
    Call<BaseResponse<LoginResult>> login(@Body LoginRequest request);
    
    @POST("api/auth/register")
    Call<BaseResponse<LoginResult>> register(@Body RegisterRequest request);
    
    @GET("api/auth/info")
    Call<BaseResponse<User>> getUserInfo();
    
    @POST("api/auth/logout")
    Call<BaseResponse<Void>> logout();
    
    public class LoginRequest {
        private String username;
        private String password;
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
    
    public class RegisterRequest {
        private String phone;
        private String realName;
        private String password;
        
        public RegisterRequest(String phone, String realName, String password) {
            this.phone = phone;
            this.realName = realName;
            this.password = password;
        }
    }
    
    public class LoginResult {
        private Long userId;
        private String username;
        private String nickname;
        private String realName;
        private String role;
        private String token;
        
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public String getRealName() { return realName; }
        public String getRole() { return role; }
        public String getToken() { return token; }
    }
}
