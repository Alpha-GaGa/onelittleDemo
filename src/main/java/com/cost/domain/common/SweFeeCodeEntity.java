package com.cost.domain.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description 斯维尔费用代号处理对象
 * @Created zhangtianhao
 * @date 2023-04-18 18:52
 * @version
 */
@Data
@Accessors(chain = true)
public class SweFeeCodeEntity implements FeeCodeEntity<SweFeeCodeItemWrapper>{
    /**
     * 费用代号
     */
    private String feeCode;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 封装数据类型(0子目 1最下层指标/清单 2清单法)
     */
    private String type;

    /**
     * 封装数据所属标识
     */
    private String characteristic;

    /**
     * 工程量
     */
    private BigDecimal quantity;

    /**
     * 工程量单位
     */
    private String unit;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 合计
     */
    private BigDecimal totalPrice;

    /**
     * 设备单价
     */
    private BigDecimal devicePrice;

    /**
     * 人工费单价
     */
    private BigDecimal workPrice;

    /**
     * 材料费单价
     */
    private BigDecimal materialPrice;

    /**
     * 材料费单价2
     */
    private BigDecimal materialPrice2;

    /**
     * 机械费单价
     */
    private BigDecimal machinePrice;

    /**
     * 管理费单价
     */
    private BigDecimal managePrice;

    /**
     * 利润费单价
     */
    private BigDecimal profitPrice;

    /**
     * 主材费单价
     */
    private BigDecimal mMaterialPrice;

    /**
     * 仪表单价
     */
    private BigDecimal meterPrice;

    /**
     * todo 这个字段待考虑
     * 调差前价格
     */
    private BigDecimal adjustBeforePrice;

    /**
     * 特殊费用
     */
    private BigDecimal specialValue;

    /**
     * 扩展信息
     */
    private String expandInfo;

    /**
     * 封装数据子集
     */
    private List<SweFeeCodeEntity> childList;
}

