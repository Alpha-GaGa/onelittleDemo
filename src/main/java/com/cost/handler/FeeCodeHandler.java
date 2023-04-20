package com.cost.handler;

import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.common.FeeCodeEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @description 费用代号分析处理器接口
 * @Created zhangtianhao
 * @date 2023-04-17 10:24
 * @version
 */
public interface FeeCodeHandler {

    /**
     * 节点的费用代号分析
     * @param item 需要解析的对象
     * @param sysFeeCodeDTO
     * @return 返回分析出来的结果
     */
    BigDecimal analysis(FeeCodeEntity item, SysFeeCodeDTO sysFeeCodeDTO);

    /**
     * 设置系统公用费用代号映射Map
     * @param systemCommonFeeCodeMapping
     */
    void setSystemCommonFeeCodeMapping(Map<String, SysFeeCodeDTO> systemCommonFeeCodeMapping);

    /**
     * 清楚处理器内缓存
     */
    void clean();

}
