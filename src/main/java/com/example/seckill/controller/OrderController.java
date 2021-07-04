package com.example.seckill.controller;

import com.example.seckill.pojo.*;
import com.example.seckill.result.CodeMsg;
import com.example.seckill.result.Result;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/get")
    public String get(){
        return "order_detail";
    }

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetail> detail(@RequestParam("orderId") long orderId, User user){

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getByOrderId(orderId);
        if(orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        long goodsId = orderInfo.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);

        OrderDetail orderDetail = new OrderDetail();

        orderDetail.setOrder(orderInfo);
        orderDetail.setGoods(goods);
        orderDetail.setUserName(user.getUserName());
        orderDetail.setUserPhone(user.getPhone());

        return Result.success(orderDetail);
    }
}
