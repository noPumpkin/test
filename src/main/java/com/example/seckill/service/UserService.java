package com.example.seckill.service;

import com.example.seckill.pojo.LoginParam;
import com.example.seckill.pojo.User;
import com.example.seckill.result.Result;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    //token值
    public static final String COOKI_NAME_TOKEN = "token";
    /**
     *
     * @param response
     * @param loginParam
     * @return
     */
    Result<String> login(LoginParam loginParam, HttpServletResponse response);

    public User getByToken(HttpServletResponse response, String token);

    public boolean register(HttpServletResponse response , String userName, String phone, String passWord , String salt);
}
