package com.example.seckill.service;

import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.OrderInfo;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    public SeckillOrder getOrderByUserIdGoodsId(int userId, long goodsId);

    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods);

    public OrderInfo getByOrderId(long orderId);
}
