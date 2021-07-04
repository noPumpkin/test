package com.example.seckill.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super(prefix);
    }

    //获取订单键值
    public static OrderKey getOrderByUidOid = new OrderKey("ok");
}
