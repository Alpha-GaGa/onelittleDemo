package com.cost.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AnalysePrice {

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
    * 分部分项id
    */
    private Long itemId;

    /**
    * 分部分项真实id
    */
    private Long itemRid;

    /**
    * 顺序
    */
    private Integer orderNum;

    /**
    * 子目录id
    */
    private Long catalogId;

    /**
    * 工料机id
    */
    private Long wmmId;

    /**
    * 工料机编号
    */
    private String wmmName;

    /**
    * 序号
    */
    private String orderNo;

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
    * 说明
    */
    private String explain;

    /**
    * jz工程费
    */
    private BigDecimal jzFee;

    /**
    * 安装工程费
    */
    private BigDecimal installFee;

    /**
    * 关税(费率)
    */
    private String tariffExpression;

    /**
    * 基础关税(费率)
    */
    private BigDecimal baseTariffRate;

    /**
    * 基础关税(费率)
    */
    private String baseTariffExpr;

    /**
    * 基础计算(未乘费率)
    */
    private BigDecimal baseCalculate;

    /**
    * 基础计算表达式(未乘费率)
    */
    private String baseCalcuExpr;

    /**
    * 其他工程费
    */
    private BigDecimal otherFee;

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