package com.example.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoodsDetail {
    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private User user;
}
