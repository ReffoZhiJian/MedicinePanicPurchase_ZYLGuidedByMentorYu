package com.zylpractice.MedicinePanicPurchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zylpractice.MedicinePanicPurchase.dto.LoginFormDTO;
import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.Purchaser;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
public interface IPurchaserService extends IService<Purchaser> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result sign();

    Result signCount();

}
