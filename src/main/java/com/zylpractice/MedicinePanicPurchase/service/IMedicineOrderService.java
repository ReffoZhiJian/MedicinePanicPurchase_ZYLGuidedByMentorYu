package com.zylpractice.MedicinePanicPurchase.service;

import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.MedicineOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
public interface IMedicineOrderService extends IService<MedicineOrder> {

    Result panicpurchaseMedicine(Long medicineId);
}
