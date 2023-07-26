package com.zylpractice.MedicinePanicPurchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 抢购限量药品表，与限量药品是一对一关系
 * </p>
 *
 * @author ZYL
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_panicpurchase_medicine")
public class PanicPurchaseMedicine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联的限量药品的id
     */
    @TableId(value = "medicine_id", type = IdType.INPUT)
    private Long medicineId;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
