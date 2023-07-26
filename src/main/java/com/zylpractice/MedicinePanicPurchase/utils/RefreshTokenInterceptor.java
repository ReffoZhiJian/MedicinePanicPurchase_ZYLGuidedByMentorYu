package com.zylpractice.MedicinePanicPurchase.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.zylpractice.MedicinePanicPurchase.dto.PurchaserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.LOGIN_USER_KEY;
import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.LOGIN_USER_TTL;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        //@ZYL：                                    ↑authorization-/再
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的购药者
        String key  = LOGIN_USER_KEY + token;
//@ZYL：-----------------------------------------------哈希的参考--------------------------------------------
        Map<Object, Object> purchaserMap = stringRedisTemplate.opsForHash().entries(key);
        // 3.判断购药者是否存在
        if (purchaserMap.isEmpty()) {
            return true;
        }
        // 5.将查询到的hash数据转为PurchaserDTO
        PurchaserDTO purchaserDTO = BeanUtil.fillBeanWithMap(purchaserMap, new PurchaserDTO(), false);
        //@ZYL：                         ↑-源，目~
        // 6.存在，保存购药者信息到 ThreadLocal
        PurchaserHolder.savePurchaser(purchaserDTO);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除购药者
        PurchaserHolder.removePurchaser();
    }
}
