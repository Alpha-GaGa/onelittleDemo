package com.cost.constant;

/**
 * @description 费用代号条件类型常量
 * @Created zhangtianhao
 * @date 2023-04-19 09:29
 * @version
 */
public interface FeeCodeConditionalTypeConstant {
    /**
     * 直接取值
     */
    String DIRECT_VALUE = "0";

    /**
     * 计算取值
     */
    String CALCULATE = "1";

    /**
     * 筛选累加
     */
    String FILTERED_ACCUMULATION = "2";

    /**
     * 筛选剔除
     */
    String FILTERED_ELIMINATE = "3";

    /**
     * 单价分析文件定义
     */
    String OTHER = "4";
}
