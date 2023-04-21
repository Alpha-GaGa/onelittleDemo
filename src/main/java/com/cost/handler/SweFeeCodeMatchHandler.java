package com.cost.handler;

import com.cost.constant.*;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.enums.FileTypeCacheKeyEnum;
import com.cost.manage.AdjustDataManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description 斯维尔费用代号处理器
 * @Created zhangtianhao
 * @date 2023-04-17 10:29
 */
@Slf4j
@Service
public class SweFeeCodeMatchHandler extends BasicsFeeCodeMatchHandler {

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

        log.info("{} 正在从 fileType={} 的 公共费用代号映射commonFeeCodeMapping 中获取 feeCode={} 的对应规则", simpleName, fileType, feeCodeWrapper.getFeeCode());
        // 先从 公共费用代号映射commonFeeCodeMapping 中获取对应的规则
        AdjustCacheKey commonKey = new AdjustCacheKey().commonFeeCodeCacheKey(feeCodeWrapper.getFileTypeCacheKeyEnum());
        SysFeeCodeDTO sysFeeCodeDTO = Optional.ofNullable(adjustDataManager.getFeeCodeMapping(commonKey))
                .map(commonFeeCodeMapping -> commonFeeCodeMapping.get(feeCodeWrapper.getFeeCode()))
                .orElseGet(() -> {
                    // 如果为空，再从 取费文件专属的feeCodeMapping 获取对应的规则
                    log.info("{} 正在从 fileType={} 、 feeDocId={} 的专属 feeCodeMapping 中获取 feeCode={} 的对应规则", simpleName, fileType, feeCodeWrapper.getFeeDocId(), feeCodeWrapper.getFeeCode());
                    AdjustCacheKey feeDocKey = new AdjustCacheKey().feeCodeCacheKey(
                            feeCodeWrapper.getFileTypeCacheKeyEnum(), feeCodeWrapper.getFeeDocId());

                    return Optional.ofNullable(adjustDataManager.getFeeCodeMapping(feeDocKey))
                            .map(feeCodeMapping -> feeCodeMapping.get(feeCodeWrapper.getFeeCode()))
                            .orElseThrow(() ->
                                    // todo 异常处理
                                    new RuntimeException(simpleName + " 处理 " + feeCodeWrapper.toString() + "时出现异常"));
                });

        // 分析规则并返回分析结果
        return analysis(feeCodeWrapper, sysFeeCodeDTO);
    }
}
