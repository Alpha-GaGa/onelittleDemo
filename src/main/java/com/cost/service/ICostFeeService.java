package com.cost.service;


import com.cost.domain.CostFee;
import com.cost.domain.request.CostFeeQueryRequest;

import java.util.List;

public interface ICostFeeService {

    /**
     * 查询费用代号和系统代号映射数据
     * @param costFeeQueryRequest
     * @return
     */
    List<CostFee> selectCostFee(CostFeeQueryRequest costFeeQueryRequest);
}
