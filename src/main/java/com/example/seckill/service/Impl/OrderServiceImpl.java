package com.example.seckill.service.Impl;

import com.example.seckill.dao.OrderMapper;
import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.OrderInfo;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.redis.OrderKey;
import com.example.seckill.redis.RedisService;
import com.example.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisService redisService;

    @Autowired
    OrderMapper orderMapper;

    @Override
    public SeckillOrder getOrderByUserIdGoodsId(int userId, long goodsId) {
        //先访问缓存
        SeckillOrder seckillOrder = redisService.get(OrderKey.getOrderByUidOid,""+userId + "_"+goodsId,SeckillOrder.class);
        if(seckillOrder == null) {
            return orderMapper.getOrderByUserIdGoodsId(userId,goodsId);
        }
        return seckillOrder;
    }

    @Override
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {
        //orderInfo
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId((long)user.getId());
        orderMapper.insert(orderInfo);

        //seckillOrder
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId((long)user.getId());
        orderMapper.insertSeckillOrder(seckillOrder);

        //把订单放入缓存,过期时间1小时
        redisService.set(OrderKey.getOrderByUidOid,""+user.getId() + "_"+goods.getId(),seckillOrder,3600);
        return orderInfo;
    }

    @Override
    public OrderInfo getByOrderId(long orderId) {
        return orderMapper.getOrderById(orderId);
    }


}
