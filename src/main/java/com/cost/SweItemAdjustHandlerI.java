package com.cost;

import com.cost.domain.CostAnalysePrice;
import com.cost.domain.common.TreeNode;
import com.cost.util.CalculateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @description 子目计算处理器
 * @Created zhangtianhao
 * @date 2023-04-10 15:47
 * @version
 */
@Slf4j
public class SweItemAdjustHandlerI extends ITreeDataHandler {

    /**
     * 系统费用代号映射Map
     */
    private Map<String, BigDecimal> systemFeeCodeMapping;

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
     * 100
     */
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /**
     * 筛选器
     */
    public static final  Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");


    public List<CostAnalysePrice> getTree(List<CostAnalysePrice> costAnalysePriceList, HashMap<String, BigDecimal> systemFeeCodeMapping){
        if(CollectionUtils.isEmpty(costAnalysePriceList)){
            throw new IllegalArgumentException("单价分析列表不能为空");
        }

        // todo 可能会改成费用代号处理器，不能为空
        if(CollectionUtils.isEmpty(systemFeeCodeMapping)){
            throw new IllegalArgumentException("系统映射不能为空");
        }

        this.systemFeeCodeMapping = systemFeeCodeMapping;

        try {
            return super.getTree(costAnalysePriceList);

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
     * @param <T> 待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void nodeProcessBefore(T node, List<T> pendingTreeNode) {
        CostAnalysePrice CostAnalysePrice = (CostAnalysePrice) node;

        // 把feeCode费用代号保存到feeCodeSet
        String feeCode = CostAnalysePrice.getFeeCode();
        feeCodeSet.add(feeCode);

        // 如果该单价分析节点wmmId为0，feeExpr为具体的值，feeRate为具体工作量，freeAmount = feeExpr * feeRate
        if (null != CostAnalysePrice.getWmmId() && CostAnalysePrice.getWmmId().equals(0L)) {
            BigDecimal FeeExpr = new BigDecimal(CostAnalysePrice.getFeeExpr());
            BigDecimal freeAmount = FeeExpr.multiply(CostAnalysePrice.getFeeRate());
            CostAnalysePrice.setFeeAmount(freeAmount);
            // 把feeCode对应的价格保存到feeCodeMapping
            feeCodeMapping.put(feeCode, freeAmount);
            return;
        }

        // 如果如果该单价分析节点wmmId为-1，feeExpr为计算方程式或为空，需要进行拆解，方程式由数字及英文字符串和( ) + - * /构成
        if (null != CostAnalysePrice.getWmmId() && CostAnalysePrice.getWmmId().equals(-1L)) {
            // 如果feeExpr非空，获取公式中包含的费用代号
            if (StringUtils.isNotBlank(CostAnalysePrice.getFeeExpr())) {
                // todo 是否除了数字、英文、_ 以为的费用代号组成
                Matcher matcher = compile.matcher(CostAnalysePrice.getFeeExpr());
                while (matcher.find()) {
                    // 把公式中包含的费用代号保存到exprFeeCodeSet
                    exprFeeCodeSet.add(matcher.group());
                }
                return;
            }

            // 如果feeExpr为空
            BigDecimal bigDecimal = BigDecimal.ZERO;
            if (!BigDecimal.ZERO.equals(CostAnalysePrice.getFeeAmount())) {
                // todo 这里需要详细补充系统代号处理器的处理逻辑？
                // 如果总价非空，先从系统费用代号映射Map中获取对应值，如果任然为空，赋值freeAmount为0，并保存到feeCodeMapping
                bigDecimal = null == systemFeeCodeMapping.get(feeCode) ? bigDecimal : systemFeeCodeMapping.get(feeCode);
            }
            CostAnalysePrice.setFeeAmount(bigDecimal);
            feeCodeMapping.put(feeCode, bigDecimal);
        }

        // todo 如果wmmId为空，或者不为0或-1，需要怎么处理
    }

    /**
     *
     * @param node 当前操作节点
     * @param groupByParentId 按照parentId分组单价分析节点Map集合
     * @param <T> 待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void nodeProcessAfter(T node, Map<Long, List<T>> groupByParentId) {
        CostAnalysePrice CostAnalysePrice = (CostAnalysePrice) node;

        // 如果当前节点的费用不为空，证明已经计算完成，直接返回
        if(null != CostAnalysePrice.getFeeAmount()) {
            return;
        }

        // 使用Aviator计算表达式
        //BigDecimal bigDecimal1 = CalculateUtils.calculateByAviator(CostAnalysePrice.getFeeExpr(), feeCodeMapping);

        // 使用calculateByJexl计算表达式
        BigDecimal bigDecimal1 = CalculateUtils.calculateByJexl(CostAnalysePrice.getFeeExpr(), feeCodeMapping);


        // 把计算值赋值为freeAmount，并保存到feeCodeMapping
        CostAnalysePrice.setFeeAmount(bigDecimal1);
        // todo 是否要考虑有相同的key情况
        feeCodeMapping.put(CostAnalysePrice.getFeeCode(), bigDecimal1);
    }

    /**
     * 在转换单价分析树形结构的前置业务处理，准备费用代号
     * @param pendingTreeNode 待处理单价分析节点List
     * @param <T> 待处理单价分析节点
     */
    @Override
    public <T extends TreeNode> void treeProcessBefore(List<T> pendingTreeNode) {
        // 过滤出exprFeeCodeSet中独有的费用代号
        Set<String> unknownfeeCodeSet = exprFeeCodeSet.stream()
                .filter(exprFeeCode -> !feeCodeSet.contains(exprFeeCode))
                .collect(Collectors.toSet());

        // todo 如果系统费用代号Map中不存在，需要怎么处理
        // 从系统费用代号映射Map中获取对应值，保存到feeCodeMapping中
        unknownfeeCodeSet.forEach(unknownfeeCode -> feeCodeMapping.put(unknownfeeCode, systemFeeCodeMapping.get(unknownfeeCode)));
    }

    /**
     *
     * @param nodeList 单价分析树形结构数据集合
     * @param groupByParentId 按照parentId分组单价分析节点Map集合
     * @param <T> 单价分析树形结构数据
     */
    @Override
    public <T extends TreeNode> void treeProcessbeAfter(List<T> nodeList, Map<Long, List<T>> groupByParentId) {

    }
}
