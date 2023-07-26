package com.zylpractice.MedicinePanicPurchase.service;

import com.zylpractice.MedicinePanicPurchase.entity.PharmacyType;
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
public interface IPharmacyTypeService extends IService<PharmacyType> {

    List<PharmacyType> queryTypeList();
}
