package com.zylpractice.MedicinePanicPurchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zylpractice.MedicinePanicPurchase.entity.Medicine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
public interface MedicineMapper extends BaseMapper<Medicine> {

    List<Medicine> queryMedicineOfPharmacy(@Param("pharmacyId") Long pharmacyId);
}
