package com.example.seckill.dao;

import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsMapper {

    @Select("select g.*, stock_count,start_date,end_date,seckill_price,version " +
            " from seckill_goods sg left join goods g on sg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.seckill_price,sg.version " +
            " from seckill_goods sg left join goods g on sg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoById(@Param("goodsId") long goodsId);

    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(@Param("goodsId")long goodsId);

    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId}")
    public int reduceStock2(@Param("goodsId")long goodsId);

    @Update("update seckill_goods set stock_count = stock_count - 1, version = version + 1 where goods_id = #{goodsId} and version = #{version}")
    public int reduceStock3(@Param("goodsId")long goodsId, @Param("version")long version);
}
