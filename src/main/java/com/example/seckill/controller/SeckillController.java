package com.example.seckill.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.example.seckill.access.AccessLimit;
import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.rabbitmq.SeckillMessage;
import com.example.seckill.redis.GoodsKey;
import com.example.seckill.redis.RedisService;
import com.example.seckill.result.CodeMsg;
import com.example.seckill.result.Result;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {

    @Autowired
    SeckillService seckillService;


    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @Autowired
    GoodsService goodsService;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount(),1800);
            localOverMap.put(goods.getId(), false);
        }
    }

    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(User user, @RequestParam("goodsId") long goodsId, @RequestParam("verifyCode") String verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        boolean check = seckillService.checkVerifyCode(Integer.valueOf(verifyCode), user.getId(), goodsId);
        if(!check){
            return Result.error(CodeMsg.VerifyCode_ERROR);
        }
        String path = seckillService.createPath(user, goodsId);
        System.out.println(path);
        return Result.success(path);
    }

    @RequestMapping(value="/{path}/seckill_mq",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckillMq(User user, @RequestParam("goodsId")long goodsId,
                                     @PathVariable("path") String path) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //??????path
        boolean checkPath = seckillService.checkPath(user, goodsId, path);
        if(!checkPath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //??????RateLimiter ??????
		RateLimiter rateLimiter = RateLimiter.create(10);
		//???????????????1????????????????????????????????????????????????false?????????????????????
		if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
			return Result.error(CodeMsg.MIAO_SHA_FAIL);
		}

        //?????????????????????redis??????
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //??????????????????????????????
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //????????????
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);//10

        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //??????????????????
        //??????
        SeckillMessage sm = new SeckillMessage();
        sm.setUser(user);
        sm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(sm);
        return Result.success(0);//?????????
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(@RequestParam("goodsId") long goodsId,User user) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/verifyCodeRegister")
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response) {
        try {
            BufferedImage image = seckillService.createVerifyCodeRegister();
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return Result.success("??????");
        } catch (Exception e) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, User user,
                                                   @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        try {
            BufferedImage image = seckillService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return Result.success("??????");
        } catch (Exception e) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

    public void test2(User user, long goodsId){
        //??????RateLimiter ??????
        RateLimiter rateLimiter = RateLimiter.create(10);
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            log.info(user.getId() + "????????????????????????????????????????????????");
            return;
        }
        //????????????
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);//10
        //log.info(String.valueOf(stock));
        if (stock < 0) {
            System.out.println(user.getId() + "???????????????");
            return;
        }

        //??????????????????????????????
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            System.out.println(user.getId() + "?????????????????????");
            return;
        }

        //??????????????????
        //??????
        SeckillMessage sm = new SeckillMessage();
        sm.setUser(user);
        sm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(sm);
        System.out.println("????????????");
        return;
    }

    public void test1(User user, long goodsId){
        String path = seckillService.createPath(user, goodsId);
        //System.out.println(path);
        test2(user, goodsId);
    }
}
