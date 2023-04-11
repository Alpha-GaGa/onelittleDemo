package com.cost.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 造价工料机汇总表
 * 
 * @author zsl
 * @email 
 * @date 2023-03-23 11:53:24
 */
@Data
public class CostWmmSum{

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
	 * 标准工料机id
	 */
	private Long standardWmmId;
	/**
	 * 标准工料机编号
	 */
	private String standardWmmCode;
	/**
	 * 工料机id
	 */
	private Long wmmId;
	/**
	 * 工料机编号
	 */
	private String wmmCode;
	/**
	 * 工料机名称
	 */
	private String wmmName;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 规格
	 */
	private String specs;
	/**
	 * 数量（工程量）
	 */
	private BigDecimal quantity;
	/**
	 * 市场价(税前信息定价)
	 */
	private BigDecimal beTaxPrice;
	/**
	 * 市场价(税后信息定价)
	 */
	private BigDecimal afTaxPrice;
	/**
	 * 定额单价(税前)
	 */
	private BigDecimal beTaxQuotaPrice;
	/**
	 * 定额单价(税后)
	 */
	private BigDecimal afTaxQuotaPrice;
	/**
	 * 合价(税前)
	 */
	private BigDecimal beTaxTotalPrice;
	/**
	 * 合价(税后)
	 */
	private BigDecimal afTaxTotalPrice;
	/**
	 * 价差合计
	 */
	private BigDecimal differTotalPrice;
	/**
	 * 市场价折税率(调整系数_信息定价)
	 */
	private BigDecimal priceRate;
	/**
	 * 综合折税率(调整系数)
	 */
	private BigDecimal synthesizeRate;
	/**
	 * 工料机类型(1人工、2材料、3机械、4物料、5主材、6设备、7进口设备)
	 */
	private String wmmType;
	/**
	 * 三材系数
	 */
	private String threeMaterialRatio;
	/**
	 * 损耗率
	 */
	private BigDecimal lossRate;
	/**
	 * 税
	 */
	private BigDecimal tax;
	/**
	 * 顺序
	 */
	private Integer orderNum;
	/**
	 * 状态（0正常 1停用）
	 */
	private String status;

}