package com.example.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetail {
    private GoodsVo goods;
    private OrderInfo order;
    private String userName;
    private String userPhone;
}
