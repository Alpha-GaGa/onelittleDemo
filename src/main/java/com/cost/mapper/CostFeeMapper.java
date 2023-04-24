package com.cost.mapper;

import com.cost.domain.CostFee;
import com.cost.domain.request.CostFeeQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CostFeeMapper {

    List<CostFee> selectCostFee(CostFeeQueryRequest costFeeQueryRequest);
}
