package com.cost.domain.common;

import java.math.BigDecimal;

/**
 * @description 费用代号对应的子目/最下层指标/清单数据封装类
 * @Created zhangtianhao
 * @date 2023-04-20 09:41
 */
public interface FeeCodeItemWrapper {

    /**
     * 获取封装数据所属名字
     * @return 封装数据所属名字
     */
    String getName();

    /**
     * 获取封装数据所属类型
     * @return 封装数据所属类型
     */
    String getType();

    /**
     * 获取封装数据所属标识
     * @return 封装数据所属标识
     */
    String getCharacteristic();

    /**
     * 获取工程量
     * @return 工程量
     */
    BigDecimal getQuantity();

    /**
     * 获取工程量单位
     * @return 工程量单位
     */
    String getUnit();

    /**
     * 获取单价
     * @return 单价
     */
    BigDecimal getPrice();

    /**
     * 获取合价
     * @return 合计
     */
    BigDecimal getTotalPrice();

    /**
     * 获取设备单价
     * @return 设备单价
     */
    BigDecimal getDevicePrice();

    /**
     * 获取人工费单价
     * @return 人工费单价
     */
    BigDecimal getWorkPrice();

    /**
     * 获取材料费单价
     * @return 材料费单价
     */
    BigDecimal getMaterialPrice();

    /**
     * 获取材料费单价2
     * @return 材料费单价2
     */
    BigDecimal getMaterialPrice2();

    /**
     * 获取机械费单价
     * @return 机械费单价
     */
    BigDecimal getMachinePrice();

    /**
     * 获取管理费单价
     * @return 管理费单价
     */
    BigDecimal getManagePrice();

    /**
     * 获取利润费单价
     * @return 利润费单价
     */
    BigDecimal getProfitPrice();

    /**
     * 获取主材费单价
     * @return 主材费单价
     */
    BigDecimal getMMaterialPrice();

    /**
     * 获取仪表单价
     * @return 仪表单价
     */
    BigDecimal getMeterPrice();

    /**
     * 获取特殊费用
     * @return 特殊费用
     */
    BigDecimal getSpecialValue();

    /**
     * 获取扩展信息（建议使用json格式）
     * @return 扩展信息
     */
    String getExpandInfo();


}
