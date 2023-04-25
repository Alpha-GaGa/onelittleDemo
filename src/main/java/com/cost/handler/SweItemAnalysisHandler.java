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
import java.util.stream.Collectors;

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
        AnalysePriceWrapper analysePriceWrapper = (AnalysePriceWrapper) node;

        String feeCode = analysePriceWrapper.getFeeCode();
        // 先把feeCode费用代号保存到feeCodeSet
        super.feeCodeSet.add(feeCode);
        // 把单价分析封装类analysePriceWrapper按照费用代号feeCode排序
        super.feeNameMapping.put(analysePriceWrapper.getFeeName(), analysePriceWrapper);

        // 如果该单价分析节点wmmName为0，feeExpr为具体的值，feeRate为具体工作量，freeAmount = feeExpr * feeRate
        if (StringUtils.isNotBlank(analysePriceWrapper.getWmmName()) && WmmNameConstant.DIRECT_VALUE.equals(analysePriceWrapper.getWmmName())) {
            BigDecimal FeeExpr = new BigDecimal(analysePriceWrapper.getFeeExpr());
            // 乘费率，但是不需要转换百%
            BigDecimal freeAmount = FeeExpr.multiply(analysePriceWrapper.getFeeRate());
            analysePriceWrapper.setFeeAmount(freeAmount);
            // 把feeCode对应的价格保存到feeCodeMapping
            super.feeCodeValueMapping.put(feeCode, freeAmount);
            // 设置给单价分析封装类已完成计算
            analysePriceWrapper.setIsCalculate(CALCULATED);
            return;
        }

        // 如果如果该单价分析节点wmmId为-1，feeExpr为计算方程式或为空，需要进行拆解，方程式由数字及英文字符串和( ) + - * /构成
        if (StringUtils.isNotBlank(analysePriceWrapper.getWmmName()) && WmmNameConstant.CALCULATE.equals(analysePriceWrapper.getWmmName())) {
            // 如果feeExpr非空，并且不是纯数字，获取公式中包含的费用代号
            if (StringUtils.isNotBlank(analysePriceWrapper.getFeeExpr()) && !analysePriceWrapper.getFeeExpr().matches("[\\d.]+")) {
                // todo 是否除了数字、英文、_ 以为的费用代号组成
                Matcher matcher = compile.matcher(analysePriceWrapper.getFeeExpr());
                while (matcher.find()) {
                    // 把公式中包含的费用代号保存到exprFeeCodeSet
                    super.exprFeeCodeSet.add(matcher.group());
                }
                return;
            }

            // 如果feeExpr为空，但是费用总价非0，需要进一步分析
            BigDecimal feeAmount = BigDecimal.ZERO;
            if (0 != BigDecimal.ZERO.compareTo(analysePriceWrapper.getFeeAmount())) {
                // 如果总价非0，分析取费代号
                feeAmount = Optional.ofNullable(analysisFeeCode(analysePriceWrapper, TYPE))
                        .orElseThrow(() ->
                                // todo 需要加异常
                                new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + "登记的系统映射资料解析 feeCode=" + analysePriceWrapper.getFeeCode())
                        );

                // 如果feeExpr不为空， 更新feeExpr参数
                if (StringUtils.isNotBlank(analysePriceWrapper.getFeeExpr())){
                    analysePriceWrapper.setFeeExpr(String.valueOf(feeAmount));
                }
                // 和费率相乘
                feeAmount = feeAmount.multiply(analysePriceWrapper.getFeeRate()).divide(ONE_HUNDRED);
                // 赋值feeAmount
                analysePriceWrapper.setFeeAmount(feeAmount);
            }

            // 保存到feeCodeValueMapping作为计算基础数据
            feeCodeValueMapping.put(feeCode, feeAmount);
            // 设置给单价分析封装类已完成计算
            analysePriceWrapper.setIsCalculate(CALCULATED);
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
        // 过滤出exprFeeCodeSet中独有的费用代号
        Set<String> unknowfeeCodeSet = exprFeeCodeSet.stream()
                .filter(exprFeeCode -> !feeCodeSet.contains(exprFeeCode))
                .collect(Collectors.toSet());

        log.info("需要处理的未确认费用代号 feeCode:{}", unknowfeeCodeSet);

        // todo 如果还是无法解析费用代号，需要怎么处理
        // 遍历unknowfeeCodeSet元素，分析，然后保存到feeCodeMapping
        unknowfeeCodeSet.forEach(unknowfeeCode -> {
            // todo 除了管理费还有其他的怎么办
            // 如果费用代号是 GLF 或者是 DJ4
            if (COMPREHENSIVE_UNIT_PRICE_FEE_CODE.equals(unknowfeeCode) || COMPREHENSIVE_UNIT_PRICE_FEE_CODE2.equals(unknowfeeCode)) {
                // 以feeCode为key，exprFeeCode为value，添加到 feeCodeTransferMapping
                feeCodeTransferMapping.put(
                        Optional.ofNullable(feeNameMapping.get(COMPREHENSIVE_UNIT_PRICE_FEE_NAME))
                                .map(AnalysePriceWrapper::getFeeCode)
                                .orElseThrow(() ->
                                        // todo 需要加异常
                                        new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() +
                                                " feeDocId=" + feeDocId +
                                                " 登记的系统映射资料解析 feeCode=" + unknowfeeCode)
                                ),
                        unknowfeeCode);
                return;
            }

            super.feeCodeValueMapping.put(
                    unknowfeeCode,
                    Optional.ofNullable(unknowfeeCode)
                            // 使用费用代号处理器分析feeCode
                            .map(feeCode -> analysisFeeCode(feeCode, TYPE))
                            .orElseThrow(() ->
                                    // todo 需要加异常
                                    new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() +
                                            " feeDocId=" + feeDocId +
                                            " 登记的系统映射资料解析 feeCode=" + unknowfeeCode)
                            ));
        });
    }
}
