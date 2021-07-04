package com.example.seckill.service.Impl;

import com.example.seckill.dao.UserMapper;
import com.example.seckill.exception.GlobalException;
import com.example.seckill.pojo.LoginParam;
import com.example.seckill.pojo.User;
import com.example.seckill.redis.RedisService;
import com.example.seckill.redis.UserKey;
import com.example.seckill.result.CodeMsg;
import com.example.seckill.result.Result;
import com.example.seckill.service.UserService;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisService redisService;

    @Override
    public Result<String> login(LoginParam loginParam, HttpServletResponse response) {
        //从数据库中查询是否有该用户
        User user = userMapper.checkPhone(loginParam.getMobile());
        //如果不存在该用户，则返回用户不存在
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String password = user.getPassword();
        String salt = user.getSalt();
        //验证密码是否正确
        String checkPassword = MD5Util.formPassToDBPass(loginParam.getPassword(), salt);
        if(!StringUtils.equals(password, checkPassword)) {
            //如果密码不一致，返回密码错误
            throw  new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //将密码设置为空
        user.setPassword(StringUtils.EMPTY);

        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        //返回成功登录
        return Result.success(token);
    }

    @Override
    public User getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        //延长有效期
        if(user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    @Override
    public boolean register(HttpServletResponse response, String userName, String phone, String passWord, String salt) {
        User user =  new User();
        user.setUserName(userName);
        user.setPhone(phone);
        String DBPassWord = MD5Util.formPassToDBPass(passWord, salt);
        user.setPassword(DBPassWord);
        user.setRegisterDate(new Date());
        user.setSalt(salt);
        try {
            User realUser = userMapper.checkPhone(user.getPhone());
            System.out.println(realUser);
            if(realUser != null){
                return false;
            }
            int success = userMapper.insertUser(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, "" + token, user, UserKey.TOKEN_EXPIRE);
        //创建一个cookie，key为token，val为token的值
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        //设置cookie过期的时间，2天
        cookie.setMaxAge(UserKey.TOKEN_EXPIRE);
        //设置cookie可用的范围
        cookie.setPath("/");
        //将cookie放入
        response.addCookie(cookie);
    }
}
