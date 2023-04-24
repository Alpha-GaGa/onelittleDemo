package com.cost.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @description 分部分项表
 * @Created zhangtianhao
 * @date 2023-04-11 10:49
 * @version
 */
@Data
public class CostItem {
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
     * 造价类型(字典关联p_cost_type，0估算 1概算 2预算 3控制价 4合同 5变更 6支付 7结算)
     */
    private String costType;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 线路项目指标id(真实指标)
     */
    private Long lineIndexId;

    /**
     * 原始id，方便刷关联数据
     */
    private String sid;

    /**
     * 原始父id，方便刷关联数据
     */
    private String spid;

    /**
     * 标准化真实id
     */
    private Long rid;

    /**
     * 标准化真实父id
     */
    private Long rpid;

    /**
     * 层级（从第2级开始，如此表到的导入数据不包含第一部分内容）
     */
    private int level;

    /**
     * 路径（本表id路径）
     */
    private String path;

    /**
     * 线路项目指标路径（t_inf_p_line_index.path节点本身id）
     */
    private String indexPath;

    /**
     * 项目编号（子目编号）
     */
    private String catalogCode;

    /**
     * 项目名称（子目名称）
     */
    private String catalogName;

    /**
     * 子目类型(字典关联，1部、3章、4节、10借、13工、17补、19类)
     */
    private String catalogType;

    /**
     * 总价取费规（按照基价计算，按照信息价计算），0-定额不含税价，1-市场不含税价，2-定额含税价，3-市场含税价
     */
    private String sumFeeRule;

    /**
     * 综合||概算(0、综合，1、概算)
     */
    private String isEstimate;

    /**
     * 章
     */
    private String tariffExpr;

    /**
     * 费用分类(字典关联)
     */
    private String feeType;

    /**
     * 概算结构
     */
    private String estimateStructure;

    /**
     * 工程量表达式
     */
    private String quantityExpr;

    /**
     * 工程量
     */
    private BigDecimal quantity;

    /**
     * 单位
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
     * 独立费
     */
    private BigDecimal aloneFee;

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
     * 特殊费用
     */
    private BigDecimal specialValue;

    /**
     * 价格分析id(单价分析)
     */
    private Long priceAnalyseId;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 措施/其他项目合价
     */
    private BigDecimal otherTotalPrice;

    /**
     * 定额分类
     */
    private String quotaType;

    /**
     * 页面分类，1分部分项、2措施项目、3其他项目、4其他
     */
    private String pegeType;

    /**
     * 排序
     */
    private Integer orderNum;

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