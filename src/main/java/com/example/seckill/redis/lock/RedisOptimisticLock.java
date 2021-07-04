package com.example.seckill.redis.lock;

import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RedisOptimisticLock {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 输出结果
     */
    public void printResult() {

        Set<String> set = redisTemplate.opsForSet().members("clientList");

        int i = 1;
        for (String value : set) {
            System.out.println("第" + i++ + "个抢到商品，"+value + " ");
        }
    }

    /*
     * 初始化顾客开始抢商品
     */
    public void initClient() throws InterruptedException {
        int clientNum = 1000;// 模拟客户数目
        for (int i = 0; i < clientNum; i++) {
            int finalI = i;
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep((int)(Math.random()*5000));// 随机睡眠一下
                    } catch (InterruptedException e1) {
                    }
                    while (true) {
                        String key = "prdNum";
                        String clientName = "" + finalI;
                        String clientList = "clientList";
                        System.out.println("顾客:" + clientName + "开始抢商品");
                        try {
                            System.out.println(key);
                            redisTemplate.watch(key);
                            int prdNum = Integer.parseInt((String) redisTemplate.opsForValue().get(key));// 当前商品个数
                            if (prdNum > 0) {
                                redisTemplate.multi();
                                redisTemplate.opsForValue().set(key, String.valueOf(prdNum - 1));
                                List<Object> result = redisTemplate.exec();
                                if (result == null || result.isEmpty()) {
                                    System.out.println("悲剧了，顾客:" + clientName + "没有抢到商品");// 可能是watch-key被外部修改，或者是数据操作被驳回
                                } else {
                                    redisTemplate.opsForSet().add(clientList, clientName);
                                    System.out.println("好高兴，顾客:" + clientName + "抢到商品");
                                    break;
                                }
                            } else {
                                System.out.println("悲剧了，库存为0，顾客:" + clientName + "没有抢到商品");
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            redisTemplate.unwatch();
                        }

                    }
                }
            }.start();
        }

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化商品个数
     */
    public void initPrduct() {
        int prdNum = 100;// 商品个数
        String key = "prdNum";
        String clientList = "clientList";// 抢购到商品的顾客列表

        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }

        if (redisTemplate.hasKey(clientList)) {
            redisTemplate.delete(clientList);
        }

        redisTemplate.opsForValue().set(key, String.valueOf(prdNum));
    }

}


