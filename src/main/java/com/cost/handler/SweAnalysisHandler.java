package com.cost.handler;

import com.cost.constant.FeeCodeScopeConstant;
import com.cost.constant.WmmNameConstant;
import com.cost.domain.common.TreeNode;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.wrapper.SweAdjustWrapper;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.enums.FileTypeCacheKeyEnum;
import com.cost.util.CalculateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @description 斯维尔单价分析处理器
 * @Created zhangtianhao
 * @date 2023-04-10 15:47
 */
public abstract class SweAnalysisHandler  extends BasicsTreeBuilder implements AnalysisHandler<AnalysePriceWrapper> {

    /**
     * 系统费用代号映射Map
     */
    private FeeCodeMatchHandler feeCodeMatchHandler;

    /**
     * 斯维尔指标/子目调差封装类
     */
    private SweAdjustWrapper adjustWrapper;

    /**
     * 文件来源类型对应cacheKey枚举类
     */
    private FileTypeCacheKeyEnum fileTypeCacheKeyEnum;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 费用代号对应AnalysePriceWrapperMapping
     */
    private final Map<String, AnalysePriceWrapper> AnalysePriceWrapperMapping = new HashMap<>();

    /**
     * 费用代号映射Map
     */
    private final Map<String, BigDecimal> feeCodeMapping = new HashMap<>();

    /**
     * 费用代号Set
     */
    private final Set<String> feeCodeSet = new HashSet<>();

    /**
     * feeExpr中费用代号Set
     */
    private final Set<String> exprFeeCodeSet = new HashSet<>();

    /**
     * 筛选器
     */
    public static final Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");


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
     * 单价分析节点前置处理，先获取其feeExpr中包含的费用代号，以及获取已知值
     *
     * @param node            当前操作节点
     * @param pendingTreeNode 待处理单价分析节点List
     * @param <T>             待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void nodeProcessBefore(T node, List<T> pendingTreeNode) {
        AnalysePriceWrapper AnalysePriceWrapper = (AnalysePriceWrapper) node;

        // 把feeCode费用代号保存到feeCodeSet
        String feeCode = AnalysePriceWrapper.getFeeCode();
        feeCodeSet.add(feeCode);
        AnalysePriceWrapperMapping.put(feeCode, AnalysePriceWrapper);

        // 如果该单价分析节点wmmName为0，feeExpr为具体的值，feeRate为具体工作量，freeAmount = feeExpr * feeRate
        if (StringUtils.isNotBlank(AnalysePriceWrapper.getWmmName()) && WmmNameConstant.DIRECT_VALUE.equals(AnalysePriceWrapper.getWmmName())) {
            BigDecimal FeeExpr = new BigDecimal(AnalysePriceWrapper.getFeeExpr());
            BigDecimal freeAmount = FeeExpr.multiply(AnalysePriceWrapper.getFeeRate());
            AnalysePriceWrapper.setFeeAmount(freeAmount);
            // 把feeCode对应的价格保存到feeCodeMapping
            feeCodeMapping.put(feeCode, freeAmount);
            return;
        }

        // 如果如果该单价分析节点wmmId为-1，feeExpr为计算方程式或为空，需要进行拆解，方程式由数字及英文字符串和( ) + - * /构成
        if (StringUtils.isNotBlank(AnalysePriceWrapper.getWmmName()) && WmmNameConstant.CALCULATE.equals(AnalysePriceWrapper.getWmmName())) {
            // 如果feeExpr非空，获取公式中包含的费用代号
            if (StringUtils.isNotBlank(AnalysePriceWrapper.getFeeExpr())) {
                // todo 是否除了数字、英文、_ 以为的费用代号组成
                Matcher matcher = compile.matcher(AnalysePriceWrapper.getFeeExpr());
                while (matcher.find()) {
                    // 把公式中包含的费用代号保存到exprFeeCodeSet
                    exprFeeCodeSet.add(matcher.group());
                }
                return;
            }

            // 如果feeExpr为空
            BigDecimal bigDecimal = BigDecimal.ZERO;
            // todo 几乎不可能，因为我们压根不取feeAmount
            if (!BigDecimal.ZERO.equals(AnalysePriceWrapper.getFeeAmount())) {
                // 如果总价非0，分析取费代号
                bigDecimal = analysisFeeCode(AnalysePriceWrapper);
            }
            AnalysePriceWrapper.setFeeAmount(bigDecimal);
            feeCodeMapping.put(feeCode, bigDecimal);
        }

        // todo 如果wmmId为空，或者不为0或-1，需要怎么处理
    }

    /**
     * 使用费用代号分析器进行分析
     *
     * @param AnalysePriceWrapper 需要分析的单价分析数据
     * @return 分析得到的结果
     */
    private BigDecimal analysisFeeCode(AnalysePriceWrapper AnalysePriceWrapper) {
        return feeCodeMatchHandler.match(
                new SweFeeCodeWrapper()
                        // 设置分析费用代号所属层级
                        .setType(FeeCodeScopeConstant.ITEM)
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
                        .setAdjustBeforePrice(new BigDecimal(AnalysePriceWrapper.getFeeExpr()))
        );
    }

    /**
     * 使用费用代号分析器进行分析
     *
     * @param feeCode 需要分析的费用代号
     * @return 分析得到的结果
     */
    private BigDecimal analysisFeeCode(String feeCode) {
        return feeCodeMatchHandler.match(
                new SweFeeCodeWrapper()
                        // 设置分析费用代号所属层级
                        .setType(FeeCodeScopeConstant.ITEM)
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

    /**
     * @param node            当前操作节点
     * @param groupByParentId 按照parentId分组单价分析节点Map集合
     * @param <T>             待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void nodeProcessAfter(T node, Map<Long, List<T>> groupByParentId) {
        AnalysePriceWrapper AnalysePriceWrapper = (AnalysePriceWrapper) node;

        // 如果当前节点的费用不为空，证明已经计算完成，直接返回
        if (null != AnalysePriceWrapper.getFeeAmount()) {
            return;
        }

        // 使用Aviator计算表达式
        //BigDecimal bigDecimal1 = CalculateUtils.calculateByAviator(CostAnalysePrice.getFeeExpr(), feeCodeMapping);

        // 使用calculateByJexl计算表达式
        BigDecimal bigDecimal1 = CalculateUtils.calculateByJexl(AnalysePriceWrapper.getFeeExpr(), feeCodeMapping);


        // 把计算值赋值为freeAmount，并保存到feeCodeMapping
        AnalysePriceWrapper.setFeeAmount(bigDecimal1);
        // todo 是否要考虑有相同的key情况
        feeCodeMapping.put(AnalysePriceWrapper.getFeeCode(), bigDecimal1);
    }

    /**
     * 在转换单价分析树形结构的前置业务处理，准备费用代号
     *
     * @param pendingTreeNode 待处理单价分析节点List
     * @param <T>             待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void treeProcessBefore(List<T> pendingTreeNode) {
        // 获取feeCodeSet和exprFeeCodeSet的差集，保存到unknowfeeCodeSet中
        Set<String> unknowfeeCodeSet = new HashSet<>(feeCodeSet);
        unknowfeeCodeSet.removeAll(exprFeeCodeSet);

        // todo 如果系统费用代号Map中不存在，需要怎么处理
        // 遍历unknowfeeCodeSet元素
        unknowfeeCodeSet.forEach(unknowfeeCode ->
        {
            if (exprFeeCodeSet.contains(unknowfeeCode)) {
                // 如果是feeExper公式里拆出来的费用代号，没有对应的单价分析数据，直接分析
                feeCodeMapping.put(unknowfeeCode, analysisFeeCode(unknowfeeCode));
            } else {
                // 如果是deeCode里的费用代号，获取对应的单价分析数据进行分析
                feeCodeMapping.put(unknowfeeCode, analysisFeeCode(AnalysePriceWrapperMapping.get(unknowfeeCode)));
            }
        });
    }

    /**
     * @param nodeList        单价分析树形结构数据集合
     * @param groupByParentId 按照parentId分组单价分析节点Map集合
     * @param <T>             单价分析树形结构数据
     */
    @Override
    public <T extends TreeNode> void treeProcessbeAfter(List<T> nodeList, Map<Long, List<T>> groupByParentId) {

    }

    @Override
    public void setFeeCodeHandler(FeeCodeMatchHandler feeCodeMatchHandler) {
        this.feeCodeMatchHandler = feeCodeMatchHandler;
    }
}
