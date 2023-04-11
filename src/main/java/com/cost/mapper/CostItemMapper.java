package com.cost.mapper;

import com.cost.domain.CostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CostItemMapper {

    /**
     *
     * @param itemId
     * @return
     */
    List<CostItem> selectCostItemListByItemId(@Param("itemId")Long itemId);

    /**
     *
     * @param parentId
     * @return
     */
    List<CostItem> selectCostItemListByParentId(@Param("parentId")Long parentId);
}
