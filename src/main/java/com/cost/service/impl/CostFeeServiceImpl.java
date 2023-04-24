package com.cost.service.impl;

import com.cost.domain.CostFee;
import com.cost.domain.request.CostFeeQueryRequest;
import com.cost.mapper.CostFeeMapper;
import com.cost.service.ICostFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostFeeServiceImpl implements ICostFeeService {

    @Autowired
    private CostFeeMapper costFeeMapper;

    @Override
    public List<CostFee> selectCostFee(CostFeeQueryRequest costFeeQueryRequest) {
        return costFeeMapper.selectCostFee(costFeeQueryRequest);
    }
}
