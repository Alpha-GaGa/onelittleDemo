package com.cost.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 造价工料机
 */
@Setter
@Getter
public class CostWmm{
    /**
     * ID
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
     * 分部分项id
     */
    private Long itemId;

    /**
     * 顺序
     */
    private Integer orderNum;

    /**
     * 造价工料机汇总id
     */
    private Long costWmmsId;

    /**
     * 工料机id
     */
    private Long wmmId;

    /**
     * 工料机编号
     */
    private String wmmCode;

    /**
     * 基础工料机编号
     */
    private String baseWmmCode;

    /**
     * 工料机类型(1人工、2材料、3机械、4物料、5主材、6设备、7进口设备)
     */
    private String wmmType;

    /**
     * 标准含量（基础工程量）
     */
    private BigDecimal standardQuantity;

    /**
     * 实际含量（真实工程量）
     */
    private BigDecimal realQuantity;

    /**
     * 实际含量表达式
     */
    private String realExpression;

    /**
     * 合同工程量
     */
    private BigDecimal contractQuantity;

    /**
     * 结算工程量
     */
    private BigDecimal settleQuantity;

    /**
     * 计算总数
     */
    private BigDecimal total;

    /**
     * 供应方
     */
    private String supplier;

    /**
     * 合算类型
     */
    private String settleType;

    /**
     * 需要保留(0需要，1不需要)
     */
    private String needRetain;

    /**
     * 不计算构成
     */
    private String isConsist;

    /**
     * 不结算联系材料费
     */
    private String isMaterialFee;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}