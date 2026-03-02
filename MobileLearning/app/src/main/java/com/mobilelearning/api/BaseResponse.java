package com.mobilelearning.api;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {
    
    @SerializedName("code")
    private Integer code;
       
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;
    
    public Integer getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public boolean isSuccess() { return code != null && code == 200; }
}
