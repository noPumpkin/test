package com.example.seckill.redis;

public class VerifyCodeKey extends BasePrefix{
    private VerifyCodeKey(String prefix) {
        super(prefix);
    }
    public static VerifyCodeKey VerifyCodeRegisterKey = new VerifyCodeKey("rk");
    public static VerifyCodeKey VerifyCodeSeckillKey = new VerifyCodeKey("sk");
}
