package com.cost.handler;

import com.cost.constant.FeeCodeScopeConstant;
import com.cost.constant.WmmNameConstant;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.common.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @description 子目计算处理器
 * @Created zhangtianhao
 * @date 2023-04-10 15:47
 */
@Slf4j
public class SweItemAnalysisHandler extends SweAnalysisHandler {

    /**
     * 最下层指标/清单类型标识
     */
    public static final String TYPE = FeeCodeScopeConstant.ITEM;

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
        super.feeCodeSet.add(feeCode);
        super.AnalysePriceWrapperMapping.put(feeCode, AnalysePriceWrapper);

        // 如果该单价分析节点wmmName为0，feeExpr为具体的值，feeRate为具体工作量，freeAmount = feeExpr * feeRate
        if (StringUtils.isNotBlank(AnalysePriceWrapper.getWmmName()) && WmmNameConstant.DIRECT_VALUE.equals(AnalysePriceWrapper.getWmmName())) {
            BigDecimal FeeExpr = new BigDecimal(AnalysePriceWrapper.getFeeExpr());
            BigDecimal freeAmount = FeeExpr.multiply(AnalysePriceWrapper.getFeeRate());
            AnalysePriceWrapper.setFeeAmount(freeAmount);
            // 把feeCode对应的价格保存到feeCodeMapping
            super.feeCodeMapping.put(feeCode, freeAmount);
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
                    super.exprFeeCodeSet.add(matcher.group());
                }
                return;
            }

            // 如果feeExpr为空
            BigDecimal feeAmount = BigDecimal.ZERO;
            // todo 几乎不可能，因为我们压根不取feeAmount
            if (!BigDecimal.ZERO.equals(AnalysePriceWrapper.getFeeAmount())) {
                // 如果总价非0，分析取费代号
                feeAmount = Optional.ofNullable(analysisFeeCode(AnalysePriceWrapper, TYPE))
                        .orElseThrow(() ->
                        // todo 需要加异常
                        new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + "登记的系统映射资料解析 feeCode:")
                );
            }
            AnalysePriceWrapper.setFeeAmount(feeAmount);
            super.feeCodeMapping.put(feeCode, feeAmount);
        }

        // todo 如果wmmId为空，或者不为0或-1，需要怎么处理
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
        Set<String> unknowfeeCodeSet = new HashSet<>(super.feeCodeSet);
        unknowfeeCodeSet.removeAll(super.exprFeeCodeSet);

        // todo 如果还是无法解析费用代号，需要怎么处理
        // 遍历unknowfeeCodeSet元素
        unknowfeeCodeSet.forEach(unknowfeeCode ->
                super.feeCodeMapping.put(
                        unknowfeeCode,
                        Optional.ofNullable(unknowfeeCode)
                                .map(feeCode -> {
                                    if (super.exprFeeCodeSet.contains(feeCode)) {
                                        // 如果是feeExper公式里拆出来的费用代号，没有对应的单价分析数据，直接分析
                                        return analysisFeeCode(feeCode, TYPE);
                                    } else {
                                        // 如果是deeCode里的费用代号，获取对应的单价分析数据进行分析
                                        return analysisFeeCode(super.AnalysePriceWrapperMapping.get(feeCode), TYPE);
                                    }
                                })
                                .orElseThrow(() ->
                                        // todo 需要加异常
                                        new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + "登记的系统映射资料解析 feeCode:")
                                )));
    }
}
