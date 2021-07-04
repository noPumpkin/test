package com.example.seckill.redis;

public class UserKey extends BasePrefix{

    //设置cook过期时间
    public static final int TOKEN_EXPIRE = 3600*24 * 2;

    private UserKey(String prefix) {
        super(prefix);
    }
    public static UserKey token = new UserKey("tk");
}
