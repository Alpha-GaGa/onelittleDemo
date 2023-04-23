package com.cost.converter;

import com.cost.domain.AnalysePrice;
import com.cost.domain.CostItem;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.wrapper.SweAdjustWrapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

/**
 * @description 单价分析封装类转换器
 * @Created zhangtianhao
 * @date 2023-04-23 16:16
 */
@Mapper(
        componentModel = "spring",
        imports = {},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AdjustWrapperConverter {

    /**
     * 单价分析转换为单价分析封装类
     *
     * @param analysePrice 单价分析
     * @return 单价分析封装类
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "lineId", source = "lineId")
    @Mapping(target = "costDocId", source = "costDocId")
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "itemId", source = "itemId")
    @Mapping(target = "orderNum", source = "orderNum")
    @Mapping(target = "wmmName", source = "wmmName")
    @Mapping(target = "orderNo", source = "orderNo")
    @Mapping(target = "feeName", source = "feeName")
    @Mapping(target = "feeCode", source = "feeCode")
    @Mapping(target = "feeExpr", source = "feeExpr")
    @Mapping(target = "feeRate", source = "feeRate")
    @Mapping(target = "feeAmount", source = "feeAmount")
    @Mapping(target = "unit", source = "unit")
    AnalysePriceWrapper analysePrice2AnalysePriceWrapper(AnalysePrice analysePrice);

    List<AnalysePriceWrapper> analysePrice2AnalysePriceWrapper(List<AnalysePrice> analysePriceList);

    /**
     * 分部分项数据转换指标/子目调差封装类
     *
     * @param costItem 分部分项数据
     * @return 指标/子目调差封装类
     */
    @Mapping(target = "lineId", source = "lineId")
    @Mapping(target = "costDocId", source = "costDocId")
    @Mapping(target = "lineIndexId", source = "lineIndexId")
    @Mapping(target = "rid", source = "rid")
    @Mapping(target = "rpid", source = "rpid")
    @Mapping(target = "level", source = "level")
    @Mapping(target = "catalogType", source = "catalogType")
    @Mapping(target = "sumFeeRule", source = "sumFeeRule")
    @Mapping(target = "estimateStructure", source = "estimateStructure")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "devicePrice", source = "devicePrice")
    @Mapping(target = "workPrice", source = "workPrice")
    @Mapping(target = "materialPrice", source = "materialPrice")
    @Mapping(target = "materialPrice2", source = "materialPrice2")
    @Mapping(target = "machinePrice", source = "machinePrice")
    @Mapping(target = "managePrice", source = "managePrice")
    @Mapping(target = "profitPrice", source = "profitPrice")
    @Mapping(target = "mMaterialPrice", source = "mMaterialPrice")
    @Mapping(target = "meterPrice", source = "meterPrice")
    @Mapping(target = "expandInfo", source = "expandInfo")
    @Mapping(target = "feeDocId", source = "feeDocId")
    @Mapping(target = "pegeType", source = "pegeType")
    SweAdjustWrapper costItem2SweAdjustWrapper(CostItem costItem);

    List<SweAdjustWrapper> costItem2SweAdjustWrapper(List<CostItem> costItemList);


}
