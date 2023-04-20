package com.cost.domain.common;

import com.cost.domain.SysFeeCodeDTO;

import java.util.Map;

/**
 * @description 斯维尔系统费用代号封装类
 * @Created zhangtianhao
 * @date 2023-04-19 23:06
 * @version
 */

public class SweSysFeeCodeWrapper implements FeeCodeWrapper{

    /**
     * 系统公用费用代号映射Map
     */
    private Map<String, SysFeeCodeDTO> sysFeeCodeMapping;


    /**
     * 匹配费用代号对应系统取费规则
     * @param feeCode 待匹配费用代号
     * @return 系统取费规则
     */
    public SysFeeCodeDTO match(String feeCode) {
        return sysFeeCodeMapping.get(feeCode);
    }

}
