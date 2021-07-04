package com.example.seckill.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderInfo {
    private Long id;

    private Long userId;

    private Long goodsId;

    private Long addrId;

    private String goodsName;

    private Integer goodsCount;

    private BigDecimal goodsPrice;

    private Integer orderChannel;

    private Integer status;

    private Date createDate;

    private Date payDate;
}
