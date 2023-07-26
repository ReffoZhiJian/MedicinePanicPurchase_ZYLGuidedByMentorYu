package com.zylpractice.MedicinePanicPurchase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.PanicPurchaseMedicine;
import com.zylpractice.MedicinePanicPurchase.entity.Medicine;
import com.zylpractice.MedicinePanicPurchase.mapper.MedicineMapper;
import com.zylpractice.MedicinePanicPurchase.service.IPanicPurchaseMedicineService;
import com.zylpractice.MedicinePanicPurchase.service.IMedicineService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
@Service
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements IMedicineService {

    @Resource
    private IPanicPurchaseMedicineService panicpurchaseMedicineService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryMedicineOfPharmacy(Long pharmacyId) {
        // 查询限量药品信息
        List<Medicine> medicines = getBaseMapper().queryMedicineOfPharmacy(pharmacyId);
        // 返回结果
        return Result.ok(medicines);
    }

    @Override
    @Transactional
    public void addPanicPurchaseMedicine(Medicine Medicine) {
        // 保存限量药品
        save(Medicine);
        // 保存抢购信息
        PanicPurchaseMedicine panicpurchaseMedicine = new PanicPurchaseMedicine();
        panicpurchaseMedicine.setMedicineId(Medicine.getId());
        panicpurchaseMedicine.setStock(Medicine.getStock());
        panicpurchaseMedicine.setBeginTime(Medicine.getBeginTime());
        panicpurchaseMedicine.setEndTime(Medicine.getEndTime());
        panicpurchaseMedicineService.save(panicpurchaseMedicine);
        // 保存抢购库存到Redis中
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + Medicine.getId(), Medicine.getStock().toString());
    }
}
