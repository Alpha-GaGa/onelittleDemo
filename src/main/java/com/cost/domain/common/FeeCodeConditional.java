package com.cost.domain.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description 系统费用代号条件对象
 * @Created zhangtianhao
 * @date 2023-04-18 13:09
 * @version
 */
@Data
@Accessors(chain = true)
public class FeeCodeConditional {
    /**
     * 执行顺序
     */
    private Integer order;

    /**
     * 条件类型(0-直接取值，1-计算取值，2-筛选累加，3-筛选剔除，4-单价分析文件定义)
     */
    private String type;

    /**
     *取值字段/表达式
     */
    private String conditionalExpr;

    /**
     * 作用对象（0-父级，1-自身，2-子级）
     */
    private String actionOn;

    /**
     * 判断条件(0-等于，1-不等于，2-大于，3-小于，4-包含)
     */
    private String judgementCondition;

    /**
     * 判断字段
     */
    private String judgementField;

    /**
     * 判断值
     */
    private String judgementValue;
}
