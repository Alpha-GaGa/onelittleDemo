package com.cost.constant;

/**
 * @description 费用代号条件作用对象常量
 * @Created zhangtianhao
 * @date 2023-04-19 10:12
 * @version
 */
public interface FeeCodeConditionalActionOnConstant {
    /**
     * 作用父类
     */
    String PARENT = "0";

    /**
     * 作用自己
     */
    String SELF = "1";

    /**
     * 作用子类
     */
    String CHILD = "2";
}
