package com.cost.domain.common;

import com.cost.domain.SysFeeCodeDTO;

/**
 * @description 系统费用代号封装类
 * @Created zhangtianhao
 * @date 2023-04-19 23:08
 * @version
 */
public interface FeeCodeWrapper {

    /**
     * 匹配费用代号对应系统取费规则
     * @param feeCode 待匹配费用代号
     * @return 系统取费规则
     */
    SysFeeCodeDTO match(String feeCode);
}
