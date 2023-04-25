package com.cost.handler;

import com.cost.constant.AdjustCacheKey;
import com.cost.constant.FeeCodeConditionalActionOnConstant;
import com.cost.constant.FeeCodeConditionalJudgeConstant;
import com.cost.constant.FeeCodeConditionalTypeConstant;
import com.cost.domain.CostFee;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.common.FeeCodeConditional;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.enums.FileTypeCacheKeyEnum;
import com.cost.manage.AdjustDataManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

/**
 * @description 斯维尔取费文件处理器
 * @Created zhangtianhao
 * @date 2023-04-22 23:31
 */
@Slf4j
@Service
public class SweCostFeeMatchHandler extends BasicsFeeCodeMatchHandler {

    /**
     * 独立费标识
     */
    public static final String INDEPENDENT_FEE_STR = "独立费";

    /**
     * 特殊费用标识
     */
    public static final String CSGD_DELX = "[CSGD_DELX]";

    /**
     * 概算结构表字段名
     */
    public static final String ESTIMATE_STRUCTURE = "estimate_structure";

    /**
     * 措施项目费条件公式
     */
    public static final String MEASURE_FEE_CONDITIONAL_EXPR = "price+profit_price";

    /**
     * 类名简称
     */
    public final String simpleName = this.getClass().getSimpleName();

    /**
     * 调差数据管理中心
     */
    @Autowired
    private AdjustDataManager adjustDataManager;

    /**
     * 节点的费用代号匹配
     *
     * @param feeCodeWrapper 需要解析的节点
     * @return 返回匹对出来的结果
     */
    @Override
    public BigDecimal match(SweFeeCodeWrapper feeCodeWrapper) {
        String fileType = feeCodeWrapper.getFileTypeCacheKeyEnum().getFileType();
        if (!FileTypeCacheKeyEnum.SWE.equals(feeCodeWrapper.getFileTypeCacheKeyEnum())) {
            throw new RuntimeException(simpleName + "只支持分析 fileType=1 数据，现传入数据 fileType=" + fileType);
        }

        // 如果单价分析名称为独立费
        if (StringUtils.isNotBlank(feeCodeWrapper.getFeeName()) && feeCodeWrapper.getFeeName().contains(INDEPENDENT_FEE_STR)) {
            // 独立费直接取费用总值
            return Optional.ofNullable(feeCodeWrapper.getFeeAmount()).orElse(null);
        }

        log.info("{} 正在从 fileType={} 的 公共费用代号映射commonFeeCodeMapping 中获取 feeCode={} 的对应规则", simpleName, fileType, feeCodeWrapper.getFeeCode());
        // 生产对应的CostFeeKey todo 需要区分线路
        AdjustCacheKey costFeeKey = new AdjustCacheKey().cosFeeeCacheKey(
                feeCodeWrapper.getFileTypeCacheKeyEnum(), feeCodeWrapper.getFeeDocId());


        // 拼装条件，目前只知道一个措施项目费
        SysFeeCodeDTO sysFeeCodeDTO = Optional.ofNullable(adjustDataManager.getCostFeeMapping(costFeeKey, feeCodeWrapper))
                .map(costFeeMapping -> {
                    CostFee costFee = costFeeMapping.get(feeCodeWrapper.getFeeCode());
                    return costFee;
                })
                .map(costFee -> {
                    // 获取取费文件的feeExpr
                    String feeExpr = costFee.getFeeExpr();
                    // todo 如果feeExpr不为空，需要再走一遍解析，大概率无用
                    if (StringUtils.isNotBlank(feeExpr)) {
                        throw new IllegalArgumentException("暂时无法解析，需要人工介入");
                    }
                    // 如果feeExpr为空，并且总价不为0，todo 目前只知道项目措施费
                    if (!BigDecimal.ZERO.equals(costFee.getFeeAmount())) {
                        // 获取费用名称
                        String feeName = costFee.getFeeName();
                        if (StringUtils.isBlank(feeName)) {
                            throw new IllegalArgumentException("Id为" + costFee.getId() + "的取费文件数据异常，没有对应的feeName");
                        }
                        // 如果取费文件feeName和单价分析feeName一致，即为措施项目费，需要从子集集合中获取对应的子目进行累加
                        if (feeName.equals(feeCodeWrapper.getFeeName())) {
                            // 创建措施项目费规则
                            return measureFee(feeCodeWrapper);
                        }
                    }
                    return null;
                })
                .orElseThrow(() ->
                        // todo 需要加异常
                        new RuntimeException(simpleName + " 处理 " + feeCodeWrapper.toString() + "时出现异常")
                );

        // 分析规则并返回分析结果
        return analysis(feeCodeWrapper, sysFeeCodeDTO);
    }

    /**
     * 创建措施项目费规则
     *
     * @param feeCodeWrapper
     * @return 措施项目费规则
     */
    private SysFeeCodeDTO measureFee(SweFeeCodeWrapper feeCodeWrapper) {
        return new SysFeeCodeDTO()
                // 设置源取费代号
                .setSourceFeeCode(feeCodeWrapper.getFeeCode())
                // 设置系统费用代号Id为0
                .setSysFeeCodeId(0L)
                // 设置取费文件Id
                .setFeeDocId(feeCodeWrapper.getFeeDocId())
                // 设置源文件类型
                .setFileType(feeCodeWrapper.getFileTypeCacheKeyEnum().getFileType())
                // 设置系统费用代号条件对象
                .setConditional(
                        Arrays.asList(
                                new FeeCodeConditional()
                                        // 设置执行顺序
                                        .setOrder(0)
                                        // 设置条件类型
                                        .setType(FeeCodeConditionalTypeConstant.FILTERED_ACCUMULATION)
                                        // 设置作用范围
                                        .setActionOn(FeeCodeConditionalActionOnConstant.CHILD)
                                        // 设置条件对象表达式
                                        .setConditionalExpr(MEASURE_FEE_CONDITIONAL_EXPR)
                                        // 设置判断条件
                                        .setJudgementCondition(FeeCodeConditionalJudgeConstant.CONTAIN)
                                        // 设置判断字段
                                        .setJudgementField(ESTIMATE_STRUCTURE)
                                        // 设置判断值
                                        .setJudgementValue(
                                                new StringBuilder(CSGD_DELX)
                                                        .append(feeCodeWrapper.getFeeName())
                                                        .toString()
                                        )
                        )
                );
    }

}
