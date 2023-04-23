package com.cost.mapper;

import com.cost.domain.AnalysePrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CostAnalysePriceMapper {

    /**
     * 获取单价分析列表
     * @param
     * @return
     */
    List<AnalysePrice> selectCostAnalysePriceList(@Param("itemId")Long itemId);

}