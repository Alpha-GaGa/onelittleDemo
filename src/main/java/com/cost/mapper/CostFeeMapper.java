package com.cost.mapper;

import com.cost.domain.CostFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CostFeeMapper {

    List<CostFee> selectCostFeeListByfeeDocId(@Param("feeDocId")Long feeDocId);
}
