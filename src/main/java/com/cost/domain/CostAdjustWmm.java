package com.cost.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 线路分部分项调差工料机表
 * @Created zhangtianhao
 * @date 2023-04-06 17:18
 * @version
 */
@Data
public class CostAdjustWmm {

    /**
    * id
    */
    private Long id;

    /**
    * 造价工料机id
    */
    private Long costWmmId;

    /**
    * 标准工料机id
    */
    private Long standardWmmId;

    /**
    * 工料机编号
    */
    private String wmmCode;

    /**
    * 建设线路id
    */
    private Long lineId;

    /**
    * 造价文件id
    */
    private Long costDocId;

    /**
    * 线路调差标准工料机表id
    */
    private Long adjustStwmmId;

    /**
    * 造价类型(字典关联p_cost_type，0估算 1概算 2预算 3控制价 4合同 5变更 6支付 7结算)
    */
    private String costType;

    /**
    * 信息价文档id
    */
    private Long priceDocId;

    /**
    * 信息价id
    */
    private Long priceId;

    /**
    * 合计调差价格
    */
    private BigDecimal totalAdjustPrice;

    /**
    * 合计调差差价
    */
    private BigDecimal totalDiffPrice;

    /**
    * 单价调差价格
    */
    private BigDecimal adjustPrice;

    /**
    * 单价调差差价
    */
    private BigDecimal adjustDiffPrice;

    /**
    * 市场价(税前信息定价)
    */
    private BigDecimal beTaxPrice;

    /**
    * 市场价(税后信息定价)
    */
    private BigDecimal afTaxPrice;

    /**
    * 调价来源(字典关联p_adjust_type，0无 1信息价 2设备价 3自定义修改)
    */
    private String adjustType;

    /**
    * 状态（0正常 1停用）
    */
    private String status;

    /**
    * 备注
    */
    private String remark;
}