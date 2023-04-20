package com.cost.demo;

import com.cost.DemoApplication;
import com.cost.SweIndexAdjustHandlerI;
import com.cost.SweItemAdjustHandlerI;
import com.cost.domain.CostAnalysePrice;
import com.cost.domain.CostFee;
import com.cost.domain.CostItem;
import com.cost.domain.CostWmm;
import com.cost.mapper.CostAnalysePriceMapper;
import com.cost.mapper.CostFeeMapper;
import com.cost.mapper.CostItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


/**
 * @description 测试类
 * @Created zhangtianhao
 * @date 2023-04-11 23:17
 * @version
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@Slf4j
public class Demo {

    @Autowired
    CostAnalysePriceMapper costAnalysePriceMapper;

    @Autowired
    CostItemMapper costItemMapper;

    @Autowired
    CostFeeMapper costFeeMapper;

    @Test
    public void testItem(){

        // 获取子目关联工料机
        CostWmm costWmm = new CostWmm();

        // 计算工料机的信息单价


        //

        // todo 获取对应itemId的单价分析
        List<CostAnalysePrice> costAnalysePriceList = costAnalysePriceMapper.selectCostAnalysePriceList(25004L);
        log.info("原始数据");
        costAnalysePriceList.forEach(System.out::println);


        HashMap<String, BigDecimal>  map = new HashMap<>();
        map.put("DJ1", new BigDecimal("120.68"));
        map.put("DJ2", new BigDecimal("14.16"));
        map.put("DJ3", new BigDecimal("5.08"));
        map.put("DJ5", new BigDecimal("120"));

        long begin = System.currentTimeMillis();
        List<CostAnalysePrice> treeList = new SweItemAdjustHandlerI().getTree(costAnalysePriceList, map);

        log.info("计算数据,共用时：{}", System.currentTimeMillis() - begin);
        treeList.forEach(System.out::println);


        // 分析单价分析公式

        // 解析

    }

    @Test
    public void testIndex(){

        Long itemId = 57411L;

        // 获取对应的index
        CostItem costItem = costItemMapper.selectCostItemListByItemId(itemId).get(0);

        if(null == costItem.getSumFeeRule()) {
            // todo 默认使用市场不含税价
            System.out.println("没有取费Id");
        }

        // 获取index的下层子目
        List<CostItem> itemList = costItemMapper.selectCostItemListByParentId(itemId);

        // 获取对应的取费文件
        List<CostFee> costFeeList = costFeeMapper.selectCostFeeListByfeeDocId(costItem.getFeeDocId());


        // 获取对应的单价分析
        List<CostAnalysePrice> costAnalysePriceList = costAnalysePriceMapper.selectCostAnalysePriceList(itemId);


        HashMap<String, BigDecimal>  map = new HashMap<>();
        map.put("RGF", costItem.getWorkPrice().multiply(costItem.getQuantity()));
        map.put("CLF", costItem.getMachinePrice().multiply(costItem.getQuantity()));
        map.put("ZCF", costItem.getMMaterialPrice().multiply(costItem.getQuantity()));
        map.put("JC_RGF", new BigDecimal("0"));
        map.put("JC_CLF", new BigDecimal("0"));
        map.put("JC_JXF", new BigDecimal("0"));
        map.put("DLF", new BigDecimal("136900"));

        // 代入计算
        long begin = System.currentTimeMillis();
        List<CostAnalysePrice> treeList = new SweIndexAdjustHandlerI().getTree(costAnalysePriceList, map, itemList, costFeeList);

        log.info("计算数据,共用时：{}", System.currentTimeMillis() - begin);
        treeList.forEach(System.out::println);
    }

}
