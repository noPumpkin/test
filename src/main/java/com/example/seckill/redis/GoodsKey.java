package com.example.seckill.redis;

public class GoodsKey extends BasePrefix{
    public GoodsKey(String prefix) {
        super(prefix);
    }
    //商品列表页键值
    public static GoodsKey getGoodsList = new GoodsKey("gl");

    //商品详情页键值
    public static GoodsKey getGoodsDetail = new GoodsKey("gd");

    //秒杀库存键值
    public static GoodsKey getSeckillGoodsStock= new GoodsKey( "gs");
}
