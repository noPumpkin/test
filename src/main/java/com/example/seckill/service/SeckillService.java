package com.example.seckill.service;

import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.OrderInfo;
import com.example.seckill.pojo.User;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;

public interface SeckillService {

    String createPath(User user, long goodsId);
    boolean checkPath(User user, long goodsId,String path);
    @Transactional
    OrderInfo seckill(User user, GoodsVo goodsVo);
    long getSeckillResult(int id, long goodsId);
    BufferedImage createVerifyCodeRegister();
    BufferedImage createVerifyCode(User user, long goodsId);
    boolean checkVerifyCodeRegister(int verifyCode);
    boolean checkVerifyCode(int verifyCode, long userId, long goodId);
}
