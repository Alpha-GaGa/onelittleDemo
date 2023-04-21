package com.cost.handler;

import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.wrapper.SweFeeCodeWrapper;

import java.math.BigDecimal;

/**
 * @description 费用代号分析处理器接口
 * @Created zhangtianhao
 * @date 2023-04-17 10:24
 * @version
 */
public interface FeeCodeMatchHandler {

    /**
     * 节点的费用代号匹配
     * @param feeCodeWrapper 需要解析的节点
     * @return 返回匹对出来的结果
     */
    BigDecimal match(SweFeeCodeWrapper feeCodeWrapper);

    /**
     * 节点的费用代号分析
     * @param feeCodeWrapper 需要解析的节点
     * @param sysFeeCodeDTO
     * @return 返回分析出来的结果
     */
    BigDecimal analysis(SweFeeCodeWrapper feeCodeWrapper, SysFeeCodeDTO sysFeeCodeDTO);

}
