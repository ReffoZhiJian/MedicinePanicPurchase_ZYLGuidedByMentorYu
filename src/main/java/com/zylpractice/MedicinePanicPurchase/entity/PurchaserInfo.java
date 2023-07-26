package com.zylpractice.MedicinePanicPurchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author ZYL
 * @since 2023-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_purchaser_info")
public class PurchaserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，购药者id
     */
    @TableId(value = "purchaser_id", type = IdType.AUTO)
    private Long purchaserId;


    /**
     * 个人介绍等信息，不超过128个字符
     */
    private String introduce;


    /**
     * 性别，0：男，1：女
     */
    private Boolean gender;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
