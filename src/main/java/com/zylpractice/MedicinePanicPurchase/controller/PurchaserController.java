package com.zylpractice.MedicinePanicPurchase.controller;


import cn.hutool.core.bean.BeanUtil;
import com.zylpractice.MedicinePanicPurchase.dto.LoginFormDTO;
import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.dto.PurchaserDTO;
import com.zylpractice.MedicinePanicPurchase.entity.Purchaser;
import com.zylpractice.MedicinePanicPurchase.entity.PurchaserInfo;
import com.zylpractice.MedicinePanicPurchase.service.IPurchaserInfoService;
import com.zylpractice.MedicinePanicPurchase.service.IPurchaserService;
import com.zylpractice.MedicinePanicPurchase.utils.PurchaserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZYL
 */
@Slf4j
@RestController
@RequestMapping("/purchaser")
public class PurchaserController {

    @Resource
    private IPurchaserService purchaserService;

    @Resource
    private IPurchaserInfoService purchaserInfoService;

    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // 发送短信验证码并保存验证码
        return purchaserService.sendCode(phone, session);
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        // 实现登录功能
        return purchaserService.login(loginForm, session);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(){
        // TODO 实现登出功能
        return Result.fail("功能未完成");
    }

    @GetMapping("/me")
    public Result me(){
        // 获取当前登录的购药者并返回
        PurchaserDTO purchaser = PurchaserHolder.getPurchaser();
        return Result.ok(purchaser);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long purchaserId){
        // 查询详情
        PurchaserInfo info = purchaserInfoService.getById(purchaserId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }

    @GetMapping("/{id}")
    public Result queryPurchaserById(@PathVariable("id") Long purchaserId){
        // 查询详情
        Purchaser purchaser = purchaserService.getById(purchaserId);
        if (purchaser == null) {
            return Result.ok();
        }
        PurchaserDTO purchaserDTO = BeanUtil.copyProperties(purchaser, PurchaserDTO.class);
        // 返回
        return Result.ok(purchaserDTO);
    }

    @PostMapping("/sign")
    public Result sign(){
        return purchaserService.sign();
    }

    @GetMapping("/sign/count")
    public Result signCount(){
        return purchaserService.signCount();
    }
}
