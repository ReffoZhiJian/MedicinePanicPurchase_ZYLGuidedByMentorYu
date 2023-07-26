package com.zylpractice.MedicinePanicPurchase.service;

import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.Medicine;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
public interface IMedicineService extends IService<Medicine> {

    Result queryMedicineOfPharmacy(Long pharmacyId);

    void addPanicPurchaseMedicine(Medicine Medicine);
}
