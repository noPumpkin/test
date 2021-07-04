package com.example.seckill.controller;

import com.example.seckill.pojo.User;
import com.example.seckill.result.CodeMsg;
import com.example.seckill.result.Result;
import com.example.seckill.service.SeckillService;
import com.example.seckill.service.UserService;
import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class RegisterController {


    @Autowired
    SeckillService seckillService;

    @Autowired
    UserService userService;

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    @PostMapping("/do_register")
    @ResponseBody
    public Result<String> do_register(@RequestParam("username") String userName,
                                   @RequestParam("phone") String phone,
                                   @RequestParam("password") String passWord,
                                   @RequestParam("verifyCode") String verifyCode,
                                   HttpServletResponse response){
        boolean check = seckillService.checkVerifyCodeRegister(Integer.valueOf(verifyCode));
        if(!check){
            System.out.println("验证码错误！");
            return Result.error(CodeMsg.VerifyCode_ERROR);
        }
        boolean registerInfo = userService.register(response, userName, phone, passWord, "1l2j3g");
        if(!registerInfo){
            System.out.println("已经有这个用户，注册失败");
            return Result.error(CodeMsg.USER_EXIST);
        }
        return Result.success("成功");
    }
}
