package com.example.seckill.service.Impl;

import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.OrderInfo;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.redis.RedisService;
import com.example.seckill.redis.SeckillKey;
import com.example.seckill.redis.VerifyCodeKey;
import com.example.seckill.redis.lock.RedisLock;
import com.example.seckill.service.GoodsService;
import com.example.seckill.service.OrderService;
import com.example.seckill.service.SeckillService;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Override
    public String createPath(User user, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "987655");
        redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,str,60);
        return str;
    }

    @Override
    public boolean checkPath(User user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String realPath = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
        return path.equals(realPath);
    }

    @Autowired
    RedisLock redisLock;

    @Override
    @Transactional
    public OrderInfo seckill(User user, GoodsVo goodsVo) {
        //redistemplate实现分布式锁
//        try {
//            //如果加锁失败
//            if (!redisLock.Lock("" + goodsVo.getId(), "" + user.getId())) {
//                return null;
//            }
//            GoodsVo goods = goodsService.getGoodsVoById(goodsVo.getId());
//            int stock = goods.getStockCount();
//            if (stock <= 0) {
//                return null;
//            }
//            System.out.println("恭喜" + user.getId() + "成功抢到");
//            //减库存
//            goodsService.reduceStock(goodsVo.getId());
//            return orderService.createOrder(user, goodsVo);
//        }finally {
//            redisLock.unlockLua("" + goodsVo.getId(), "" + user.getId());
//        }
        //数据库实现乐观锁
//        int flag = goodsService.reduceStock(goodsVo.getId());
//        if(flag == 1) {
//            System.out.println(user.getId() + "成功秒杀"+ goodsVo.getId() + "号商品");
//            return orderService.createOrder(user, goodsVo);
//        }
//        else {
//            //已无库存
//            setGoodsOver(goodsVo.getId());
//            return null;
//        }
        int count = 0;
        while(count <= 3){
            GoodsVo goods = goodsService.getGoodsVoById(goodsVo.getId());
            int stock = goods.getStockCount();
            if(stock <= 0) {
                //已无库存
                setGoodsOver(goodsVo.getId());
                return null;
            }

            int flag = goodsService.reduceStock(goodsVo.getId(), goodsVo.getVersion());
            if(flag == 1) {
                System.out.println(user.getId() + "成功秒杀"+ goodsVo.getId() + "号商品");
                return orderService.createOrder(user, goodsVo);
            }
            count++;
        }
        return null;
    }

    @Override
    public long getSeckillResult(int id, long goodsId) {
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(id, goodsId);
        if(order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true , 3600);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }

    @Override
    public BufferedImage createVerifyCodeRegister() {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(VerifyCodeKey.VerifyCodeRegisterKey, "", rnd, 300);
        //输出图片
        return image;
    }

    @Override
    public BufferedImage createVerifyCode(User user, long goodsId) {
        if(user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(VerifyCodeKey.VerifyCodeRegisterKey, "" + user.getId() + "_" + goodsId, rnd, 300);
        //输出图片
        return image;
    }


    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Integer catch1 = (Integer)engine.eval(exp);
            return catch1.intValue();
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    @Override
    public boolean checkVerifyCodeRegister(int verifyCode) {
        Integer codeOld = redisService.get(VerifyCodeKey.VerifyCodeRegisterKey,"", Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(VerifyCodeKey.VerifyCodeRegisterKey, "");
        return true;
    }

    @Override
    public boolean checkVerifyCode(int verifyCode, long  userId, long goodId) {
        Integer codeOld = redisService.get(VerifyCodeKey.VerifyCodeRegisterKey,"" + userId + "_" + goodId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(VerifyCodeKey.VerifyCodeSeckillKey, "" + userId + "_" + goodId);
        return true;
    }


}
