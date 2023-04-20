package com.cost.domain.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @description 斯维尔费用代号对应的子目/最下层指标/清单数据封装类
 * @Created zhangtianhao
 * @date 2023-04-20 09:45
 */
@Data
@Accessors(chain = true)
public class SweFeeCodeItemWrapper implements FeeCodeItemWrapper {

    /**
     * 封装数据所属名字
     */
    private String name;

    /**
     * 封装数据所属类型
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
     * 特殊费用
     */
    private BigDecimal specialValue;

    /**
     * 扩展信息
     */
    private String expandInfo;
}
