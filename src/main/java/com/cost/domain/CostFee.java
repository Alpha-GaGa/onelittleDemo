package com.cost.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 造价取费文件表
 * @Created zhangtianhao
 * @date 2023-04-11 10:50
 * @version
 */
@Data
public class CostFee {

    /**
    * id
    */
    private Long id;

    /**
    * 建设路线id
    */
    private Long lineId;

    /**
    * 造价文件id
    */
    private Long costDocId;

    /**
    * 父节点id
    */
    private Long parentId;

    /**
    * 取费文件id
    */
    private Long feeDocId;

    /**
    * 子目id
    */
    private Long catalogId;

    /**
    * 原始id
    */
    private Long sid;

    /**
    * 原始父id
    */
    private Long spid;

    /**
    * 顺序
    */
    private Integer orderNum;

    /**
    * 序号
    */
    private String orderNo;

    /**
    * 分表格名称
    */
    private String girdName;

    /**
    * 费用名称
    */
    private String feeName;

    /**
    * 费用代号
    */
    private String feeCode;

    /**
    * 费用表达式
    */
    private String feeExpr;

    /**
    * 费率(%)
    */
    private BigDecimal feeRate;

    /**
    * 费用金额
    */
    private BigDecimal feeAmount;

    /**
    * 单位
    */
    private String unit;

    /**
    * 是否输出
    */
    private String isOut;

    /**
    * 计算总数
    */
    private BigDecimal total;

    /**
    * 说明
    */
    private String explain;

    /**
    * 状态（0正常 1停用）
    */
    private String status;

    /**
    * 创建者
    */
    private String createBy;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 更新者
    */
    private String updateBy;

    /**
    * 更新时间
    */
    private Date updateTime;

    /**
    * 备注
    */
    private String remark;
}