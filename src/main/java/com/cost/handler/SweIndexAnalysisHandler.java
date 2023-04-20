package com.cost.handler;

import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.CostItem;
import com.cost.domain.CostFee;
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
 * @description 最下层指标/清单的计算处理器
 * @Created zhangtianhao
 * @date 2023-04-10 15:47
 */
@Slf4j
public class SweIndexAnalysisHandler extends BasicsTreeBuilder {

    /**
     * 系统费用代号映射Map
     */
    private Map<String, BigDecimal> systemFeeCodeMapping;

    /**
     * 取费文件表
     */
    private Map<String, CostFee> costFeeMapping;

    /**
     * 子目列表
     */
    private List<CostItem> costItemList;

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
     * 独立费标识
     */
    public static final String  INDEPENDENT_FEE_STR = "独立费";

    /**
     * 特殊费用标识
     */
    public static final String  CSGD_DELX = "[CSGD_DELX]";

    /**
     * 筛选器
     */
    public static final Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");


    public List<AnalysePriceWrapper> getTree(List<AnalysePriceWrapper> analysePriceWrapperList, HashMap<String, BigDecimal> systemFeeCodeMapping, List<CostItem> costItemList, List<CostFee> costFeeList) {
        if (CollectionUtils.isEmpty(analysePriceWrapperList)) {
            throw new IllegalArgumentException("费用组成列表不能为空");
        }

        if (CollectionUtils.isEmpty(systemFeeCodeMapping)) {
            throw new IllegalArgumentException("系统映射不能为空");
        }

        if (CollectionUtils.isEmpty(costItemList)) {
            throw new IllegalArgumentException("下层子目不能为空");
        }

        if (CollectionUtils.isEmpty(costFeeList)) {
            throw new IllegalArgumentException("取费文件不能为空");
        }

        costFeeMapping = new HashMap<String, CostFee>();
        costFeeList.forEach(costFee -> costFeeMapping.put(costFee.getFeeCode(), costFee));

        this.systemFeeCodeMapping = systemFeeCodeMapping;

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

        String feeCode = AnalysePriceWrapper.getFeeCode();
        // 把feeCode费用代号保存到feeCodeSet
        feeCodeSet.add(feeCode);

        // 如果该单价分析节点wmmId为0，feeExpr为具体的值，feeRate为具体工作量，freeAmount = feeExpr * feeRate
        if (null != AnalysePriceWrapper.getWmmId() && AnalysePriceWrapper.getWmmId().equals(0L)) {
            BigDecimal FeeExpr = new BigDecimal(AnalysePriceWrapper.getFeeExpr());
            BigDecimal freeAmount = FeeExpr.multiply(AnalysePriceWrapper.getFeeRate());
            AnalysePriceWrapper.setFeeAmount(freeAmount);
            // 把feeCode对应的价格保存到feeCodeMapping
            feeCodeMapping.put(feeCode, freeAmount);
            return;
        }

        // 如果如果该单价分析节点wmmId为-1，feeExpr为计算方程式或为空，需要进行拆解，方程式由数字及英文字符串和( ) + - * /构成
        if (null != AnalysePriceWrapper.getWmmId() && AnalysePriceWrapper.getWmmId().equals(-1L)) {
            // 如果feeExpr非空，并且不是纯数字，获取公式中包含的费用代号
            if (StringUtils.isNotBlank(AnalysePriceWrapper.getFeeExpr()) && !AnalysePriceWrapper.getFeeExpr().matches("[\\d.]+")) {
                    // todo 是否除了数字、英文、_ 以为的费用代号组成
                    Matcher matcher = compile.matcher(AnalysePriceWrapper.getFeeExpr());
                    while (matcher.find()) {
                        // 把公式中包含的费用代号保存到exprFeeCodeSet
                        exprFeeCodeSet.add(matcher.group());
                    }
            }


            // 如果feeExpr为空
            BigDecimal feeAmount = BigDecimal.ZERO;
            // 如果总价不为0
            c1:if (!BigDecimal.ZERO.equals(AnalysePriceWrapper.getFeeAmount())) {
                // 如果单价分析名称为独立费
                if(AnalysePriceWrapper.getFeeName().contains(INDEPENDENT_FEE_STR)){
                    // 独立费直接取费用总值
                    feeAmount = AnalysePriceWrapper.getFeeAmount();
                    break c1;
                }

                // todo 这里需要详细补充系统代号处理器的处理逻辑？
                // 如果总价不为0，并且不是独立费，先从系统费用代号映射Map中获取对应值
                BigDecimal result = systemFeeCodeMapping.get(feeCode);
                // 如果系统费用代号无法获取，需要从取费文件中获取
                if(null == result) {
                    // 获取对应feeCode的取费文件数据
                    CostFee costFee = costFeeMapping.get(feeCode);
                    // 获取取费文件的feeExpr
                    String feeExpr = costFee.getFeeExpr();
                    // todo 如果feeExpr不为空，需要再走一遍解析，大概率无用
                    if(StringUtils.isNotBlank(feeExpr)){
                        throw new IllegalArgumentException("暂时无法解析，需要人工介入");
                    }
                    // 如果feeExpr为空，并且总价不为0，即为措施项目费，需要从子集集合中获取对应的子目进行累加
                    if(!BigDecimal.ZERO.equals(costFee.getFeeAmount())){
                        // 从取费文件获取措施项目费名称
                        String feeName = costFee.getFeeName();
                        if(StringUtils.isBlank(feeName)){
                            throw new IllegalArgumentException("Id为" + costFee.getId() + "的取费文件数据异常，没有对应的feeName");
                        }
                        // todo 从子目中筛选对应的措施项目费，并累加（合价 + 利润合价）
                        String identityStr = new StringBuffer(CSGD_DELX).append(feeName).toString();
                        BigDecimal finalFeeAmount = feeAmount;
                        costItemList.stream()
                                .filter(costItem -> null != costItem.getEstimateStructure() && costItem.getEstimateStructure().contains(identityStr))
                                .forEach(costItem -> finalFeeAmount.add(costItem.getTotalPrice()).add(costItem.getProfitPrice().multiply(costItem.getQuantity())));
                        feeAmount = finalFeeAmount;
                    }
                }
            }
            AnalysePriceWrapper.setFeeAmount(feeAmount);
            feeCodeMapping.put(feeCode, feeAmount);
        }

        // todo 如果wmmId为空，或者不为0或-1，需要怎么处理
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
        // 过滤出exprFeeCodeSet中独有的费用代号
        Set<String> unknownfeeCodeSet = exprFeeCodeSet.stream()
                .filter(exprFeeCode -> !feeCodeSet.contains(exprFeeCode))
                .collect(Collectors.toSet());

        // todo 如果系统费用代号Map中不存在，需要怎么处理
        // 从系统费用代号映射Map中获取对应值，保存到feeCodeMapping中
        unknownfeeCodeSet.forEach(unknownfeeCode -> {

            // todo 这里需要详细补充系统代号处理器的处理逻辑？
            BigDecimal bigDecimal = systemFeeCodeMapping.get(unknownfeeCode);
            // todo 如果系统代号处理器的逻辑无法解决，回到取费文件中寻找
            if (null == bigDecimal) {

            }
            feeCodeMapping.put(unknownfeeCode, bigDecimal);
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
}
