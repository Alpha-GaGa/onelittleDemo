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
import java.util.stream.Collectors;

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
        AnalysePriceWrapper analysePriceWrapper = (AnalysePriceWrapper) node;

        String feeCode = analysePriceWrapper.getFeeCode();
        // 先把feeCode费用代号保存到feeCodeSet
        super.feeCodeSet.add(feeCode);
        // 把单价分析封装类analysePriceWrapper按照费用代号feeCode排序
        super.analysePriceWrapperMapping.put(feeCode, analysePriceWrapper);

        // 如果该单价分析节点wmmName为0，feeExpr为具体的值，feeRate为具体工作量，freeAmount = feeExpr * feeRate
        if (StringUtils.isNotBlank(analysePriceWrapper.getWmmName()) && WmmNameConstant.DIRECT_VALUE.equals(analysePriceWrapper.getWmmName())) {
            BigDecimal FeeExpr = new BigDecimal(analysePriceWrapper.getFeeExpr());
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
                    exprFeeCodeSet.add(matcher.group());
                }
                return;
            }

            // 如果feeExpr为空，但是费用总价非0，需要进一步分析
            BigDecimal feeAmount = BigDecimal.ZERO;
            if (!BigDecimal.ZERO.equals(analysePriceWrapper.getFeeAmount())) {
                // todo 这里需要详细补充系统代号处理器的处理逻辑？
                // 如果总价非0，分析取费代号
                feeAmount = Optional.ofNullable(analysePriceWrapper)
                        .map(wrapper -> {
                            BigDecimal result = analysisFeeCode(analysePriceWrapper, TYPE);
                            // 如果费用代号处理器无法解析出结果，需要到取费文件中寻找
                            if (null == result) {
                                result = analysisCostFee(analysePriceWrapper);
                            }
                            return result;
                        })
                        .orElseThrow(() ->
                                // todo 需要加异常
                                new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + "登记的系统映射资料解析 feeCode=" + analysePriceWrapper.getFeeCode())
                        );
            }
            analysePriceWrapper.setFeeAmount(feeAmount);
            feeCodeValueMapping.put(feeCode, feeAmount);
            // 设置给单价分析封装类已完成计算
            analysePriceWrapper.setIsCalculate(CALCULATED);
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
        // 过滤出exprFeeCodeSet中独有的费用代号
        Set<String> unknowfeeCodeSet = exprFeeCodeSet.stream()
                .filter(exprFeeCode -> !feeCodeSet.contains(exprFeeCode))
                .collect(Collectors.toSet());

        log.info("需要处理的未确认费用代号 feeCode:{}", unknowfeeCodeSet);

        // todo 如果还是无法解析费用代号，需要怎么处理
        // 遍历unknowfeeCodeSet元素，分析，然后保存到feeCodeMapping
        unknowfeeCodeSet.forEach(unknowfeeCode -> {
            // 如果费用代号是 GLF
            if (COMPREHENSIVE_UNIT_PRICE_FEE_CODE.equals(unknowfeeCode)) {
                // 如果费用名称是 管理费
                AnalysePriceWrapper analysePriceWrapper = analysePriceWrapperMapping.get(unknowfeeCode);
                if (COMPREHENSIVE_UNIT_PRICE_FEE_NAME.equals(analysePriceWrapper.getFeeName())) {
                    // 添加 GLF = DJ4
                    super.feeCodeTransferMapping.put(unknowfeeCode, COMPREHENSIVE_UNIT_PRICE_FEE_CODE2);
                    return;
                }
                // todo 需要加异常
                throw new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + " 登记的系统映射资料解析 feeCode=" + unknowfeeCode);

            }

            super.feeCodeValueMapping.put(
                    unknowfeeCode,
                    Optional.ofNullable(unknowfeeCode)
                            .map(feeCode -> {
                                BigDecimal result = null;
                                // 使用费用代号处理器分析feeCode
                                result = analysisFeeCode(feeCode, TYPE);

                                // 如果费用代号处理器无法解析出结果，需要用取费文件处理器分析
                                if (null == result) {
                                    result = analysisCostFee(super.analysePriceWrapperMapping.get(feeCode));
                                }

                                return result;
                            })
                            .orElseThrow(() ->
                                    // todo 需要加异常
                                    new RuntimeException("无法通过 fileType=" + fileTypeCacheKeyEnum.getFileType() + " feeDocId=" + feeDocId + " 登记的系统映射资料解析 feeCode=" + unknowfeeCode)
                            ));
        });

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
