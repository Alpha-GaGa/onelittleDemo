package com.cost.handler;

import com.cost.constant.FeeCodeScopeConstant;
import com.cost.constant.WmmNameConstant;
import com.cost.domain.wrapper.AnalysePriceWrapper;
import com.cost.domain.common.TreeNode;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @description 最下层指标/清单的计算处理器
 * @Created zhangtianhao
 * @date 2023-04-10 15:47
 */
@Slf4j
@Data
public class SweIndexAnalysisHandler extends SweAnalysisHandler {

    /**
     * 系统费用代号映射Map
     */
    private FeeCodeMatchHandler sweCostFeeMatchHandler;

    /**
     * 最下层指标/清单类型标识
     */
    public static final String TYPE = FeeCodeScopeConstant.INDEX;

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
            if (!BigDecimal.ZERO.equals(AnalysePriceWrapper.getFeeAmount())) {
                // todo 这里需要详细补充系统代号处理器的处理逻辑？
                // 如果总价非0，分析取费代号
                feeAmount = Optional.ofNullable(AnalysePriceWrapper)
                        .map(wrapper -> {
                            BigDecimal result = analysisFeeCode(AnalysePriceWrapper, TYPE);
                            // 如果费用代号处理器无法解析出结果，需要到取费文件中寻找
                            if (null == result) {
                                result = analysisCostFee(AnalysePriceWrapper);
                            }
                            return result;
                })
                        .orElseThrow(() ->
                        // todo 需要加异常
                        new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + "登记的系统映射资料解析 feeCode:")
                );
            }
            AnalysePriceWrapper.setFeeAmount(feeAmount);
            feeCodeMapping.put(feeCode, feeAmount);
        }

        // todo 如果WmmName为空，或者不为0或-1，需要怎么处理
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
                                    BigDecimal result = null;
                                    if (super.exprFeeCodeSet.contains(feeCode)) {
                                        // 如果是feeExper公式里拆出来的费用代号，没有对应的单价分析数据，直接分析
                                        result = analysisFeeCode(feeCode, TYPE);
                                    } else {
                                        // 如果是deeCode里的费用代号，获取对应的单价分析数据进行分析
                                        result = analysisFeeCode(super.AnalysePriceWrapperMapping.get(feeCode), TYPE);
                                    }
                                    // 如果费用代号处理器无法解析出结果，需要到取费文件中寻找
                                    if (null == result) {
                                        result = analysisCostFee(super.AnalysePriceWrapperMapping.get(feeCode));
                                    }
                                    return result;
                                })
                                .orElseThrow(() ->
                                        // todo 需要加异常
                                        new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + "登记的系统映射资料解析 feeCode:")
                                )));

    }

    /**
     * 使用取费文件分析器进行分析
     *
     * @param AnalysePriceWrapper 需要分析的单价分析数据
     * @return 分析得到的结果
     */
    public BigDecimal analysisCostFee(AnalysePriceWrapper AnalysePriceWrapper) {
        return sweCostFeeMatchHandler.match(
                new SweFeeCodeWrapper()
                        // 设置分析费用代号所属层级
                        .setType(FeeCodeScopeConstant.INDEX)
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
}
