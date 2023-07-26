package com.zylpractice.MedicinePanicPurchase.controller;


import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.PharmacyType;
import com.zylpractice.MedicinePanicPurchase.service.IPharmacyTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZYL
 */
@RestController
@RequestMapping("/pharmacy-type")
public class PharmacyTypeController {

    @Resource
    private IPharmacyTypeService typeService;

    @GetMapping("list")
    public Result queryTypeList() {
        List<PharmacyType> typeList = typeService.queryTypeList();
        return Result.ok(typeList);
    }
}
