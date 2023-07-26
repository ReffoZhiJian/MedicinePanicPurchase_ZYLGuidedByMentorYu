package com.zylpractice.MedicinePanicPurchase.service.impl;

import com.zylpractice.MedicinePanicPurchase.entity.PanicPurchaseMedicine;
import com.zylpractice.MedicinePanicPurchase.mapper.PanicPurchaseMedicineMapper;
import com.zylpractice.MedicinePanicPurchase.service.IPanicPurchaseMedicineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 抢购限量药品表，与限量药品是一对一关系 服务实现类
 * </p>
 *
 * @author ZYL
 * @since 2022-01-04
 */
@Service
public class PanicPurchaseMedicineServiceImpl extends ServiceImpl<PanicPurchaseMedicineMapper, PanicPurchaseMedicine> implements IPanicPurchaseMedicineService {

}
