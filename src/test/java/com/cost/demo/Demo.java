package com.cost.demo;

import com.cost.DemoApplication;
import com.cost.constant.FeeCodeScopeConstant;
import com.cost.constant.FileTypeConstant;
import com.cost.converter.AdjustWrapperConverter;
import com.cost.domain.*;
import com.cost.domain.request.FeeCodeQueryRequest;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.wrapper.SweAdjustWrapper;
import com.cost.factory.AnalysisHandlerFactory;
import com.cost.handler.SweIndexAnalysisHandler;
import com.cost.handler.SweItemAnalysisHandler;
import com.cost.mapper.CostAnalysePriceMapper;
import com.cost.mapper.CostFeeMapper;
import com.cost.mapper.CostItemMapper;
import com.cost.mapper.FeeCodeRelMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Autowired
    AnalysisHandlerFactory analysisHandlerFactory;

    @Autowired
    FeeCodeRelMapper feeCodeRelMapper;

    @Autowired
    @Qualifier(value = "adjustWrapperConverterImpl")
    AdjustWrapperConverter adjustWrapperConverter;

    @Test
    public void testItem(){

        long begin = System.currentTimeMillis();

        // 获取子目 id = 53256和 id = 53257
        CostItem costItem = costItemMapper.selectCostItemListByItemId(53256L);

        // 处理子目数据，封装到标准类SweAdjustWrapper，为计算准备基本参数
        SweAdjustWrapper sweAdjustWrapper = adjustWrapperConverter.costItem2SweAdjustWrapper(costItem);


        // 获取子目单价分析处理器
        SweItemAnalysisHandler analysisHandler = (SweItemAnalysisHandler)analysisHandlerFactory.
                getAnalysisHandler(sweAdjustWrapper, FileTypeConstant.SWE_FILE, FeeCodeScopeConstant.ITEM);


        // 获取子目对应的单价分析
        List<AnalysePrice> analysePriceList = costAnalysePriceMapper.selectCostAnalysePriceList(53256L);
        log.info("原始数据");
        analysePriceList.forEach(System.out::println);

        // 处理单价分析数据，封装到标准类AnalysePriceWrapper，为计算准备基本参数
        List<AnalysePriceWrapper> analysePriceWrapperList = adjustWrapperConverter.analysePrice2AnalysePriceWrapper(analysePriceList);

        // 进行单价分析，获取有完整结果的单价分析树
        List<AnalysePriceWrapper> analysis = analysisHandler.analysis(analysePriceWrapperList);
        log.info("计算数据");
        analysis.forEach(System.out::println);

        // 通过单价分析树结果补充子目数据


        log.info("计算数据,共用时：{}", System.currentTimeMillis() - begin);
    }

    @Test
    public void testIndex(){

        Long itemId = 57411L;

        // 获取对应的index
        CostItem costItem = costItemMapper.selectCostItemListByItemId(itemId);

        if(null == costItem.getSumFeeRule()) {
            // todo 默认使用市场不含税价
            System.out.println("没有取费Id");
        }

        // 获取index的下层子目
        List<CostItem> itemList = costItemMapper.selectCostItemListByParentId(itemId);

        // 获取对应的取费文件
        List<CostFee> costFeeList = costFeeMapper.selectCostFeeListByfeeDocId(costItem.getFeeDocId());


        // 获取对应的单价分析
        List<AnalysePrice> analysePriceList = costAnalysePriceMapper.selectCostAnalysePriceList(itemId);


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
    }

    @Test
    public void testFeeCodeRel(){
        List<SysFeeCodeDTO> sysFeeCodeDTOS = feeCodeRelMapper.selectFeeCode(
                new FeeCodeQueryRequest()
                        .setFeeDocId(21L)
                        .setFileType("1"));

        sysFeeCodeDTOS.forEach(System.out::println);

    }

}
