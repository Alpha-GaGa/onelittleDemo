package com.cost.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 线路项目指标总造价调差对象
 * @Created zhangtianhao
 * @date 2023-04-19 09:19
 * @version
 */
@Data
@Accessors(chain = true)
public class CostLineIndexCostAdjust {
    
    /**
    * id
    */
    private Long id;

    /**
    * 建设线路id
    */
    private Long lineId;

    /**
    * 线路项目指标id
    */
    private Long lineIndexId;

    /**
    * 线路指标造价id(关联t_inf_p_line_index_cost表id)
    */
    private Long indexCostId;

    /**
    * 分部分项id
    */
    private Long itemId;

    /**
    * 造价文件id
    */
    private Long costDocId;

    /**
    * 造价类型(字典关联p_cost_type，0估算 1概算 2预算 3控制价 4合同 5变更 6支付 7结算)
    */
    private String costType;

    /**
    * 信息价id
    */
    private Long priceId;

    /**
    * 子目类型(字典关联，1部、3章、4节、8清、10借、定、11费、12独、13工、16计工日、17补、19类)
    */
    private String catalogType;

    /**
    * 总价取费规（按照基价计算，按照信息价计算），0-定额不含税价，1-市场不含税价，2-定额含税价，3-市场含税价
    */
    private String sumFeeRule;

    /**
    * 价格分析id(单价分析)
    */
    private Long priceAnalyseId;

    /**
    * 取费文件id
    */
    private Long feeDocId;

    /**
    * 调差单价
    */
    private BigDecimal adjustPrice;

    /**
    * 调差单价价差
    */
    private BigDecimal adjustDiffPrice;

    /**
    * 调差合价
    */
    private BigDecimal totalAdjustPrice;

    /**
    * 调差合价价差
    */
    private BigDecimal totalDiffPrice;

    /**
    * 调差设备单价
    */
    private BigDecimal adjustDevicePrice;

    /**
    * 调差人工费单价
    */
    private BigDecimal adjustWorkPrice;

    /**
    * 调差材料费单价
    */
    private BigDecimal adjustMaterialPrice;

    /**
    * 调差材料费单价2
    */
    private BigDecimal adjustMaterialPrice2;

    /**
    * 调差机械费单价
    */
    private BigDecimal adjustMachinePrice;

    /**
    * 调差管理费单价
    */
    private BigDecimal adjustManagePrice;

    /**
    * 调差利润费单价
    */
    private BigDecimal adjustProfitPrice;

    /**
    * 调差主材费单价
    */
    private BigDecimal adjustMMaterialPrice;

    /**
    * 调差仪表单价
    */
    private BigDecimal adjustMeterPrice;

    /**
    * 调差设备合价
    */
    private BigDecimal totalDevicePrice;

    /**
    * 调差人工费合价
    */
    private BigDecimal totalWorkPrice;

    /**
    * 调差材料费合价
    */
    private BigDecimal totalMaterialPrice;

    /**
    * 调差材料费合价2
    */
    private BigDecimal totalMaterialPrice2;

    /**
    * 调差机械费合价
    */
    private BigDecimal totalMachinePrice;

    /**
    * 调差管理费合价
    */
    private BigDecimal totalManagePrice;

    /**
    * 调差利润费合价
    */
    private BigDecimal totalProfitPrice;

    /**
    * 调差主材费合价
    */
    private BigDecimal totalMMaterialPrice;

    /**
    * 调差仪表合价
    */
    private BigDecimal totalMeterPrice;

    /**
    * 状态（0正常 1停用）
    */
    private String tatus;

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