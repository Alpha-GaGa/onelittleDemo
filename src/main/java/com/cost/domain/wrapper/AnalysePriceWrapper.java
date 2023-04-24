package com.cost.domain.wrapper;


import com.cost.domain.common.TreeNode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description 单价分析封装类
 * @Created zhangtianhao
 * @date 2023-04-20 20:20
 * @version
 */
@Data
public class AnalysePriceWrapper implements TreeNode<AnalysePriceWrapper> {

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
	 * 父节点ID
	 */
	private Long parentId;
	/**
	 * 分部分项id
	 */
	private Long itemId;
	/**
	 * 顺序
	 */
	private Integer orderNum;
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
	 * 是否已经计算，false表示未计算，true表示已计算，默认值为false
	 */
	private Boolean isCalculate = false;
	/**
	 * 子集
	 */
	private List<AnalysePriceWrapper> children;


}
