package com.zylpractice.MedicinePanicPurchase.controller;


import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.Medicine;
import com.zylpractice.MedicinePanicPurchase.service.IMedicineService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZYL
 */
@RestController
@RequestMapping("/Medicine")
public class MedicineController {

    @Resource
    private IMedicineService medicineService;

    /**
     * 新增抢购药品
     * @param Medicine 限量药品信息，包含抢购信息
     * @return 限量药品id
     */
    @PostMapping("panicpurchase")
    public Result addPanicPurchaseMedicine(@RequestBody Medicine Medicine) {
        medicineService.addPanicPurchaseMedicine(Medicine);
        return Result.ok(Medicine.getId());
    }

    /**
     * 新增普通药品
     * @param Medicine 限量药品信息
     * @return 限量药品id
     */
    @PostMapping
    public Result addMedicine(@RequestBody Medicine Medicine) {
        medicineService.save(Medicine);
        return Result.ok(Medicine.getId());
    }


    /**
     * 查询药房的限量药品列表
     * @param pharmacyId 药房id
     * @return 限量药品列表
     */
    @GetMapping("/list/{pharmacyId}")
    public Result queryMedicineOfPharmacy(@PathVariable("pharmacyId") Long pharmacyId) {
       return medicineService.queryMedicineOfPharmacy(pharmacyId);
    }
}
