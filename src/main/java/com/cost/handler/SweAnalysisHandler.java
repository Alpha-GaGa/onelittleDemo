package com.cost.handler;

import com.cost.domain.common.TreeNode;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.wrapper.SweAdjustWrapper;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.enums.FileTypeCacheKeyEnum;
import com.cost.util.CalculateUtils;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @description 斯维尔单价分析处理器
 * @Created zhangtianhao
 * @date 2023-04-10 15:47
 */
@Accessors(chain = true)
public abstract class SweAnalysisHandler extends BasicsTreeBuilder implements AnalysisHandler<AnalysePriceWrapper> {

    /**
     * 系统费用代号映射Map
     */
    FeeCodeMatchHandler feeCodeMatchHandler;

    /**
     * 斯维尔指标/子目调差封装类
     */
    SweAdjustWrapper adjustWrapper;

    /**
     * 文件来源类型对应cacheKey枚举类
     */
    FileTypeCacheKeyEnum fileTypeCacheKeyEnum;

    /**
     * 取费文件id
     */
    Long feeDocId;

    /**
     * 费用名称对应AnalysePriceWrapperMapping
     */
    final Map<String, AnalysePriceWrapper> feeNameMapping = new HashMap<>();

    /**
     * 费用代号对应值Map
     */
    final Map<String, BigDecimal> feeCodeValueMapping = new HashMap<>();

    /**
     * 费用代号对应费用代号中间Map
     */
    final Map<String, String> feeCodeTransferMapping = new HashMap<>();

    /**
     * 费用代号Set
     */
    final Set<String> feeCodeSet = new HashSet<>();

    /**
     * feeExpr中费用代号Set
     */
    final Set<String> exprFeeCodeSet = new HashSet<>();

    /**
     * 筛选器
     */
    public static final Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");

    /**
     * 管理费费用名称
     */
    public static final String COMPREHENSIVE_UNIT_PRICE_FEE_NAME = "管理费";
    /**
     * 管理费费用代号feeCode
     */
    public static final String COMPREHENSIVE_UNIT_PRICE_FEE_CODE = "GLF";
    /**
     * 管理费费用代号2feeCode
     */
    public static final String COMPREHENSIVE_UNIT_PRICE_FEE_CODE2 = "DJ4";

    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");


    /**
     * @param analysePriceWrapperList
     * @return
     */
    @Override
    public List<AnalysePriceWrapper> analysis(List<AnalysePriceWrapper> analysePriceWrapperList) {
        if (CollectionUtils.isEmpty(analysePriceWrapperList)) {
            throw new IllegalArgumentException("单价分析列表不能为空");
        }
        try {
            return super.getTree(analysePriceWrapperList);
            // todo 需要做处理
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param node            当前操作节点
     * @param groupByParentId 按照parentId分组单价分析节点Map集合
     * @param <T>             待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void nodeProcessAfter(T node, Map<Long, List<T>> groupByParentId) {
        AnalysePriceWrapper analysePriceWrapper = (AnalysePriceWrapper) node;

        // 如果当前节点已经计算完成，直接返回
        if (analysePriceWrapper.getIsCalculate()) {
            return;
        }

        // 使用Aviator计算表达式
        BigDecimal bigDecimal = CalculateUtils.calculateByAviator(analysePriceWrapper.getFeeExpr(), feeCodeValueMapping);

        // 使用calculateByJexl计算表达式
        //BigDecimal bigDecimal = CalculateUtils.calculateByJexl(analysePriceWrapper.getFeeExpr(), feeCodeMapping);

        // 和费率相乘获得结果
        BigDecimal result = bigDecimal.multiply(analysePriceWrapper.getFeeRate()).divide(ONE_HUNDRED);

        // 如果节点feeCode在feeCodeTransferMapping中间Map有对应key，需要对value进行赋值
        String feeCodeTransferValue = feeCodeTransferMapping.get(analysePriceWrapper.getFeeCode());
        if (StringUtils.isNotBlank(feeCodeTransferValue)) {
            // 赋值value与该节点fee相同
            feeCodeValueMapping.put(feeCodeTransferValue, result);
        }


        // 把计算值赋值为freeAmount，并保存到feeCodeMapping
        analysePriceWrapper.setFeeAmount(result);
        // todo 是否要考虑有相同的key情况
        feeCodeValueMapping.put(analysePriceWrapper.getFeeCode(), result);
        // 设置给单价分析封装类已完成计算
        analysePriceWrapper.setIsCalculate(CALCULATED);
    }

    /**
     * @param nodeList        单价分析树形结构数据集合
     * @param groupByParentId 按照parentId分组单价分析节点Map集合
     * @param <T>             单价分析树形结构数据
     */
    @Override
    public <T extends TreeNode> void treeProcessbeAfter(List<T> nodeList, Map<Long, List<T>> groupByParentId) {

    }

    /**
     * 使用费用代号分析器进行分析
     *
     * @param AnalysePriceWrapper 需要分析的单价分析数据
     * @param type                需要分析的单价分析所属类型(0子目 1最下层指标/清单 2清单法)
     * @return 分析得到的结果
     */
    public BigDecimal analysisFeeCode(AnalysePriceWrapper AnalysePriceWrapper, String type) {
        return feeCodeMatchHandler.match(
                new SweFeeCodeWrapper()
                        // 设置分析费用代号所属层级
                        .setType(type)
                        // 设置待分析费用代号所属取费文件Id
                        .setFeeDocId(feeDocId)
                        // 设置待分析费用代号来源
                        .setFileTypeCacheKeyEnum(fileTypeCacheKeyEnum)
                        // 设置待分析费用代号所属节点数据
                        .setAdjustWrapper(adjustWrapper)
                        // 设置待分析费用代号名称
                        .setFeeName(AnalysePriceWrapper.getFeeName())
                        // 设置待分析费用代号
                        .setFeeCode(AnalysePriceWrapper.getFeeCode())
                        // 设置调差前的价格
                        .setAdjustBeforePrice(new BigDecimal(AnalysePriceWrapper.getFeeExpr()))
                        // 设置单价分析原来的总价
                        .setFeeAmount(AnalysePriceWrapper.getFeeAmount())
        );
    }

    /**
     * 使用费用代号分析器进行分析
     *
     * @param feeCode 需要分析的费用代号
     * @param type    需要分析的单价分析所属类型(0子目 1最下层指标/清单 2清单法)
     * @return 分析得到的结果
     */
    public BigDecimal analysisFeeCode(String feeCode, String type) {
        return feeCodeMatchHandler.match(
                new SweFeeCodeWrapper()
                        // 设置分析费用代号所属层级
                        .setType(type)
                        // 设置待分析费用代号所属取费文件Id
                        .setFeeDocId(feeDocId)
                        // 设置待分析费用代号来源
                        .setFileTypeCacheKeyEnum(fileTypeCacheKeyEnum)
                        // 设置待分析费用代号所属节点数据
                        .setAdjustWrapper(adjustWrapper)
                        // 设置待分析费用代号
                        .setFeeCode(feeCode)
        );
    }

    public void setFeeCodeMatchHandler(FeeCodeMatchHandler feeCodeMatchHandler) {
        this.feeCodeMatchHandler = feeCodeMatchHandler;
    }

    public void setAdjustWrapper(SweAdjustWrapper adjustWrapper) {
        this.adjustWrapper = adjustWrapper;
    }

    public void setFileTypeCacheKeyEnum(FileTypeCacheKeyEnum fileTypeCacheKeyEnum) {
        this.fileTypeCacheKeyEnum = fileTypeCacheKeyEnum;
    }

    public void setFeeDocId(Long feeDocId) {
        this.feeDocId = feeDocId;
    }

    public SweAnalysisHandler() {
    }
}
