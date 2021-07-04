package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.redis.lock.RedisLock;
import com.example.seckill.redis.lock.RedisOptimisticLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

@Controller
public class TestController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisOptimisticLock redisOptimisticLock;

    @Autowired
    RedisLock redisLock;

    @Autowired
    SeckillController seckillController;

    @RequestMapping("/test")
    @ResponseBody
    public String test() throws InterruptedException {
//        long starTime=System.currentTimeMillis();
//
//        redisOptimisticLock.initPrduct();
//        redisOptimisticLock.initClient();
//        redisOptimisticLock.printResult();
//
//        long endTime=System.currentTimeMillis();
//        long Time=endTime-starTime;
//        System.out.println("程序运行时间： "+Time+"ms");
//        redisLock.Lock("goods1", "id1");
//        Thread.sleep(30000);
//        redisLock.unlockLua("goods1", "id1");

        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            new Thread(){
                @Override
                public void run() {
                    User user = new User(1000 + finalI, "user" + finalI,
                            "15238981" + finalI, "83a1b4fcb2f8d3af5fabfe6562020e10",
                            "1l2j3g", null, 1, null, null);
                    seckillController.test1(user, 3);
                }
            }.start();
        }
        return "success";
    }
}
