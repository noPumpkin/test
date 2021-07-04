package com.example.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods{

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    private BigDecimal seckillPrice;

    private Long version;
}
