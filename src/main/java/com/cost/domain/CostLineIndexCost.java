package com.cost.domain;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 线路项目指标总造价表
 * 
 * @author zsl
 * @email 
 * @date 2023-03-24 15:55:36
 */
@Data
public class CostLineIndexCost{

	/**
	 * ID
	 */
	private Long id;
	/**
	 * 建设路线id
	 */
	private Long lineId;
	/**
	 * 线路项目指标id
	 */
	private Long lineIndexId;
	/**
	 * 造价类型(字典关联p_cost_type，0估算 1概算 2预算 3控制价 4合同 5变更 6支付 7结算)
	 */
	private String costType;
	/**
	 * 工程量
	 */
	private BigDecimal quantity;
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
	 * 建筑工程概预算价值
	 */
	private BigDecimal buildValue;
	/**
	 * 安装工程概预算价值
	 */
	private BigDecimal installValue;
	/**
	 * 设备购置费概预算价值
	 */
	private BigDecimal deviceValue;
	/**
	 * 其他项目概预算价值
	 */
	private BigDecimal otherValue;
	/**
	 * 措施/其他项目合价
	 */
	private BigDecimal otherTotalPrice;
	/**
	 * 设备合价
	 */
	private BigDecimal totalDevicePrice;
	/**
	 * 人工费合价
	 */
	private BigDecimal totalWorkPrice;
	/**
	 * 材料费合价
	 */
	private BigDecimal totalMaterialPrice;
	/**
	 * 材料费合价2
	 */
	private BigDecimal totalMaterialPrice2;
	/**
	 * 机械费合价
	 */
	private BigDecimal totalMachinePrice;
	/**
	 * 管理费合价
	 */
	private BigDecimal totalManagePrice;
	/**
	 * 利润费合价
	 */
	private BigDecimal totalProfitPrice;
	/**
	 * 主材费合价
	 */
	private BigDecimal totalMMaterialPrice;
	/**
	 * 仪表合价
	 */
	private BigDecimal totalMeterPrice;
	/**
	 * 状态（0正常 1停用）
	 */
	private String status;

}
