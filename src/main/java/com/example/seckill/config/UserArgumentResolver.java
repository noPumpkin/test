package com.example.seckill.config;

import com.example.seckill.access.UserContext;
import com.example.seckill.pojo.User;
import com.example.seckill.service.UserService;
import io.lettuce.core.dynamic.support.MethodParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    UserService userService;

    @Override
    public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz== User.class;
    }

    @Override
    public Object resolveArgument(org.springframework.core.MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }
}
