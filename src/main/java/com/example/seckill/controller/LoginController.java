package com.example.seckill.controller;

import com.example.seckill.pojo.LoginParam;
import com.example.seckill.result.Result;
import com.example.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    UserService userService;


    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(@Valid LoginParam loginParam, HttpServletResponse response) {
        return userService.login(loginParam, response);
    }
}
