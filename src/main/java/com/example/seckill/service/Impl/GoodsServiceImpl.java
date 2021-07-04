package com.example.seckill.service.Impl;

import com.example.seckill.dao.GoodsMapper;
import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.SeckillGoods;
import com.example.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoById(long goodsId) {
        return goodsMapper.getGoodsVoById(goodsId);
    }

    @Override
    public int reduceStock(long goodsId, long version) {
        return goodsMapper.reduceStock3(goodsId, version);
    }

}
