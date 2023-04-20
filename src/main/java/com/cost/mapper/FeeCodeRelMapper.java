package com.cost.mapper;

import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.request.FeeCodeQueryRequest;

import java.util.List;

/**
 * @description 费用代号映射持久层
 * @Created zhangtianhao
 * @date 2023-04-19 17:06
 */
public interface FeeCodeRelMapper {

    /**
     * 查询费用代号和系统代号映射数据
     *
     * @param feeCodeQueryRequest
     * @return
     */
    List<SysFeeCodeDTO> selectFeeCode(FeeCodeQueryRequest feeCodeQueryRequest);
}
