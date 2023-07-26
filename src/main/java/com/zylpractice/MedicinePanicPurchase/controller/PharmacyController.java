package com.zylpractice.MedicinePanicPurchase.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.Pharmacy;
import com.zylpractice.MedicinePanicPurchase.service.IPharmacyService;
import com.zylpractice.MedicinePanicPurchase.utils.SystemConstants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZYL
 */
@RestController
@RequestMapping("/pharmacy")
public class PharmacyController {

    @Resource
    public IPharmacyService pharmacyService;

    /**
     * 根据id查询药房信息
     * @param id 药房id
     * @return 药房详情数据
     */
    @GetMapping("/{id}")
    public Result queryPharmacyById(@PathVariable("id") Long id) {
        return pharmacyService.queryById(id);
    }

    /**
     * 新增药房信息
     * @param pharmacy 药房数据
     * @return 药房id
     */
    @PostMapping
    public Result savePharmacy(@RequestBody Pharmacy pharmacy) {
        // 写入数据库
        pharmacyService.save(pharmacy);
        // 返回药房id
        return Result.ok(pharmacy.getId());
    }

    /**
     * 更新药房信息
     * @param pharmacy 药房数据
     * @return 无
     */
    @PutMapping
    public Result updatePharmacy(@RequestBody Pharmacy pharmacy) {
        // 写入数据库
        return pharmacyService.update(pharmacy);
    }

    /**
     * 根据药房类型分页查询药房信息
     * @param typeId 药房类型
     * @param current 页码
     * @return 药房列表
     */
    @GetMapping("/of/type")
    public Result queryPharmacyByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Double y
    ) {
       return pharmacyService.queryPharmacyByType(typeId, current, x, y);
    }

    /**
     * 根据药房名称关键字分页查询药房信息
     * @param name 药房名称关键字
     * @param current 页码
     * @return 药房列表
     */
    @GetMapping("/of/name")
    public Result queryPharmacyByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Pharmacy> page = pharmacyService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 返回数据
        return Result.ok(page.getRecords());
    }
}
