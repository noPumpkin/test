package com.example.seckill.controller;

import com.example.seckill.pojo.GoodsDetail;
import com.example.seckill.pojo.GoodsVo;
import com.example.seckill.pojo.User;
import com.example.seckill.redis.GoodsKey;
import com.example.seckill.redis.RedisService;
import com.example.seckill.result.Result;
import com.example.seckill.service.GoodsService;
import com.sun.deploy.net.HttpResponse;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/list")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model) {

        String html;

        html = redisService.get(GoodsKey.getGoodsDetail, "", String.class);

        if(html != null) {
            return html;
        }

        List<GoodsVo> goodsVos = goodsService.listGoodsVo();

        for (GoodsVo goodsVo : goodsVos) {
            System.out.println(goodsVo);
        }

        model.addAttribute("goodsVos", goodsVos);
        IWebContext ctx =new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());

        html = engine.process("goods_list", ctx);

        if(!StringUtils.isEmpty(html)) {

            redisService.set(GoodsKey.getGoodsList, "", html , 60);
        }

        return html;
    }

    @RequestMapping("/detail")
    @ResponseBody
    public String goodsDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @RequestParam("goodsId")long goodsId) {

        model.addAttribute("user", user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        //根据id查询秒杀商品
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        model.addAttribute("goods",goods);

        //获取开始、介绍、现在的时间,转换为毫秒
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        //秒杀状态、倒计时
        int seckillStatus = 0;
        int reSeconds = 0;

        if(now < start) { //秒杀未开始，倒计时
            seckillStatus = 0;
            reSeconds = (int)((start-now)/1000);
        } else if(now > end) {  //秒杀已结束
            seckillStatus = 2;
            reSeconds = -1;
        } else {   //秒杀进行中
            seckillStatus = 1;
            reSeconds = 0;
        }
        model.addAttribute("reSeconds",reSeconds);
        model.addAttribute("seckillStatus",seckillStatus);

        IWebContext ctx =new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());

        html = engine.process("goods_detail", ctx);

        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html , 60);
        }
        return html;
    }

    @RequestMapping("/detailStatic/{goodsId}")
    @ResponseBody
    public Result<GoodsDetail> detailStatic(@PathVariable("goodsId")long goodsId, User user) {
        //根据id查询秒杀商品
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        //获取开始、介绍、现在的时间,转换为毫秒
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        //秒杀状态、倒计时
        int seckillStatus = 0;
        int reSeconds = 0;

        if(now < start) { //秒杀未开始，倒计时
            seckillStatus = 0;
            reSeconds = (int)((start-now)/1000);
        } else if(now > end) {  //秒杀已结束
            seckillStatus = 2;
            reSeconds = -1;
        } else {   //秒杀进行中
            seckillStatus = 1;
            reSeconds = 0;
        }
        GoodsDetail detail = new GoodsDetail();
        detail.setGoods(goods);
        detail.setUser(user);
        detail.setRemainSeconds(reSeconds);
        detail.setSeckillStatus(seckillStatus);

        return Result.success(detail);
    }
}
