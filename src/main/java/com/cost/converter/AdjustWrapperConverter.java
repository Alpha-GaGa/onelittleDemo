package com.cost.converter;

import com.cost.domain.AnalysePrice;
import com.cost.domain.CostItem;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.wrapper.SweAdjustWrapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

/**
 * @description 单价分析封装类转换器
 * @Created zhangtianhao
 * @date 2023-04-23 16:16
 */
@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AdjustWrapperConverter {

    /**
     * 单价分析转换为单价分析封装类
     *
     * @param analysePrice 单价分析
     * @return 单价分析封装类
     */
    AnalysePriceWrapper analysePrice2AnalysePriceWrapper(AnalysePrice analysePrice);

    List<AnalysePriceWrapper> analysePrice2AnalysePriceWrapper(List<AnalysePrice> analysePriceList);

    /**
     * 分部分项数据转换指标/子目调差封装类
     *
     * @param costItem 分部分项数据
     * @return 指标/子目调差封装类
     */
    SweAdjustWrapper costItem2SweAdjustWrapper(CostItem costItem);

    List<SweAdjustWrapper> costItem2SweAdjustWrapper(List<CostItem> costItemList);


}
