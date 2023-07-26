package com.zylpractice.MedicinePanicPurchase.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    //@ZYL：                                          ↑-登录业务的code验证~
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:pharmacy:";

    public static final String LOCK_SHOP_KEY = "lock:pharmacy:";
    public static final Long LOCK_SHOP_TTL = 10L;

    public static final String SECKILL_STOCK_KEY = "panicpurchase:stock:";
    public static final String BLOG_LIKED_KEY = "medicine:collected:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "pharmacy:geo:";
    public static final String USER_SIGN_KEY = "sign:";
    public static final String ZYL_HASH_KEY = "hashKey";
}
