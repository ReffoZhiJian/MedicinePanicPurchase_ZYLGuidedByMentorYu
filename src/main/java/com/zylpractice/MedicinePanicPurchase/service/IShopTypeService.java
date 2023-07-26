package com.hmdp.service;

import com.hmdp.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
public interface IShopTypeService extends IService<ShopType> {

    List<ShopType> queryTypeList();
}
