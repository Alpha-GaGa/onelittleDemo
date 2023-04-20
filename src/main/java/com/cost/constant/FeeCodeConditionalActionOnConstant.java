package com.cost.constant;

/**
 * @description 费用代号条件作用对象常量
 * @Created zhangtianhao
 * @date 2023-04-19 10:12
 * @version
 */
public interface FeeCodeConditionalActionOnConstant {
    /**
     * 直接取值
     */
    String PARENT = "0";

    /**
     * 计算取值
     */
    String SELF = "1";

    /**
     * 筛选累加
     */
    String CHILD = "2";
}
