package com.example.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SeckillOrder {
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;
}
