package com.zylpractice.MedicinePanicPurchase.controller;


import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.service.IMedicineOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZYL
 */
@RestController
@RequestMapping("/Medicine-order")
public class MedicineOrderController {

    @Resource
    private IMedicineOrderService medicineOrderService;

    @PostMapping("panicpurchase/{id}")
    public Result panicpurchaseMedicine(@PathVariable("id") Long medicineId) {
        return medicineOrderService.panicpurchaseMedicine(medicineId);
    }
}
