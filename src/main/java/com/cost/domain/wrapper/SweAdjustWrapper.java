package com.cost.domain.wrapper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description 指标/子目调差封装类
 * @Created zhangtianhao
 * @date 2023-04-20 20:31
 * @version
 */
@Data
public class SweAdjustWrapper {
    /**
     * 建设路线id
     */
    private Long lineId;

    /**
     * 造价文件id
     */
    private Long costDocId;

    /**
     * 线路项目指标id(真实指标)
     */
    private Long lineIndexId;

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
     * 子目类型(字典关联，1部、3章、4节、10借、13工、17补、19类)
     */
    private String catalogType;

    /**
     * 总价取费规（按照基价计算，按照信息价计算），0-定额不含税价，1-市场不含税价，2-定额含税价，3-市场含税价
     */
    private String sumFeeRule;

    /**
     * 概算结构（措施项目费的判断字段）
     */
    private String estimateStructure;

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
     * 扩展信息
     */
    private String expandInfo;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 页面分类，1分部分项、2措施项目、3其他项目、4其他
     */
    private String pegeType;

    /**
     * 封装数据子集
     */
    private List<SweAdjustWrapper> childList;
}
