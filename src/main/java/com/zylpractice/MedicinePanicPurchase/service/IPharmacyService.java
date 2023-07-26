package com.zylpractice.MedicinePanicPurchase.service;

import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.Pharmacy;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
public interface IPharmacyService extends IService<Pharmacy> {

    Result queryById(Long id);

    Result update(Pharmacy pharmacy);

    Result queryPharmacyByType(Integer typeId, Integer current, Double x, Double y);
}
