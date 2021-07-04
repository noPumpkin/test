package com.example.seckill.service;

import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.SeckillGoods;

import java.util.List;

public interface GoodsService {

    public List<GoodsVo> listGoodsVo();

    public GoodsVo getGoodsVoById(long goodsId);

    public int reduceStock(long goodsId, long version);
}
