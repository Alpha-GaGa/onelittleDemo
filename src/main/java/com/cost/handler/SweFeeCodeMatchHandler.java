package com.cost.handler;

import com.cost.common.redis.service.RedisService;
import com.cost.constant.*;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.wrapper.FeeCodeWrapper;
import com.cost.domain.request.FeeCodeQueryRequest;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.enums.FileTypeRedisKeyEnum;
import com.cost.service.IFeeCodeRelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;

/**
 * @description 斯维尔费用代号处理器
 * @Created zhangtianhao
 * @date 2023-04-17 10:29
 */
@Slf4j
@Service
public class SweFeeCodeMatchHandler extends BasicsFeeCodeMatchHandler {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IFeeCodeRelService feeCodeRelService;

    /**
     * 系统公用费用代号映射Map
     */
    private Map<String, SysFeeCodeDTO> sysCommonFeeCodeMapping;

    /**
     * 取费文件专属费用代号映射Map
     */
    private Map<Long, Map<String, SysFeeCodeDTO>> feeDocFeeCodeMapping;

    /**
     * 类名简称
     */
    public final String simpleName = this.getClass().getSimpleName();


    /**
     * 节点的费用代号匹配
     * @param feeCodeWrapper 需要解析的节点
     * @return 返回匹对出来的结果
     */
    @Override
    public BigDecimal match(SweFeeCodeWrapper feeCodeWrapper) {
        log.info("{} 正在获取从 systemCommonFeeCodeMapping 中获取 {} 费用代号对应规则", simpleName, feeCodeWrapper.getFeeCode());
        // 先从systemCommonFeeCodeMapping中获取对应的规则
        SysFeeCodeDTO sysFeeCodeDTO = sysCommonFeeCodeMapping.get(feeCodeWrapper.getFeeCode());
        // 如果为空，从FeeDocFeeCodeMapping获取对应的规则
        if (null == sysFeeCodeDTO) {
            log.info("{} 正在获取从 feeDocFeeCodeMapping 中获取 {} 费用代号对应规则", simpleName, feeCodeWrapper.getFeeCode());
            sysFeeCodeDTO = matchByFeeDocFeeCodeMapping(feeCodeWrapper);
        }

        // 处理规则并返回
        return analysis(feeCodeWrapper, sysFeeCodeDTO);
    }

    /**
     * 节点的费用代号匹配 取费文件专属费用代号映射Map
     * @param feeCodeWrapper
     * @return
     */
    public SysFeeCodeDTO matchByFeeDocFeeCodeMapping(SweFeeCodeWrapper feeCodeWrapper) {
        // 通过feeDocId获取取费文件专属费用代号对应规则
        Map<String, SysFeeCodeDTO> feeCodeMapping = feeDocFeeCodeMapping.get(
                Optional.ofNullable(feeCodeWrapper)
                        .map(SweFeeCodeWrapper::getFeeDocId)
                        .orElseThrow(() ->
                                new RuntimeException(simpleName + " 处理" + "费用代号 feeCode " + feeCodeWrapper.getFeeCode() + "需要提供 feeDocId")));

        // 如果feeCodeMapping非空，获取feeCode对应的规则
        if (!CollectionUtils.isEmpty(feeCodeMapping) && null != feeCodeMapping.get(feeCodeWrapper.getFeeCode())) {
            return feeCodeMapping.get(feeCodeWrapper.getFeeCode());
        }

        // 如果为空，重新读取，组装redisKey
        String sweFeeCodeRedisKey = FeeCodeRedisKey.getFeeCodeRedisKey(FileTypeRedisKeyEnum.SWE, feeCodeWrapper.getFeeDocId());

        if (Boolean.TRUE.equals(redisService.hasKey(sweFeeCodeRedisKey))) {
            // 从缓存中获取
            log.info("正在从Reids获取斯维尔 feeDocId 为 {} 的专属费用代号对应规则 redisKey{} outTime {}", feeCodeWrapper.getFeeCode(), sweFeeCodeRedisKey, redisService.getExpire(sweFeeCodeRedisKey));
            feeCodeMapping = redisService.getCacheObject(sweFeeCodeRedisKey);
        } else {
            // 从数据库中获取
            log.info("正在从数据库获取斯维尔 feeDocId 为 {} 的专属费用代号对应规则", feeCodeWrapper.getFeeDocId());
            List<SysFeeCodeDTO> feeCodeDTOList = feeCodeRelService.selectFeeCode(
                    new FeeCodeQueryRequest()
                            .setFeeDocId(feeCodeWrapper.getFeeDocId())
                            .setFileType(FileTypeConstant.SWE_FILE)
            );

            // 处理数据，sourceFeeCode为key保存到HashMap中
            feeCodeMapping = feeCodeDTOList.stream().collect(Collectors.toMap(SysFeeCodeDTO::getSourceFeeCode, sysFeeCodeDTO -> sysFeeCodeDTO));

            log.info("保存斯维尔 feeDocId 为 {} 的专属费用代号对应规则到Redis redisKey{} outTime {}{}", feeCodeWrapper.getFeeDocId(), sweFeeCodeRedisKey, FeeCodeRedisKey.EXPIRATION, TimeUnit.MINUTES);
            redisService.setCacheObject(sweFeeCodeRedisKey, feeCodeMapping, FeeCodeRedisKey.EXPIRATION, TimeUnit.MINUTES);
        }

        // 从保存到feeDocFeeCodeMapping并获取对应
        return Optional.ofNullable(feeCodeMapping).map(map ->
                {
                    log.info("{} 保存斯维尔 feeDocId 为 {} 的专属费用代号对应规则到feeDocFeeCodeMapping", simpleName, feeCodeWrapper.getFeeDocId());
                    // 添加进取费文件专属费用代号映射Map
                    feeDocFeeCodeMapping.put(feeCodeWrapper.getFeeDocId(), map);
                    return map.get(feeCodeWrapper.getFeeCode());
                })
                // todo 需要跟换异常和打印日志
                .orElseThrow(() ->
                        new RuntimeException(simpleName + "无法找到 feeDocId " + feeCodeWrapper.getFeeDocId() + " feeCode " + feeCodeWrapper.getFeeCode() + "费用代号对应规则"));
    }

    /**
     * 设置系统公用费用代号映射Map
     *
     * @param sysCommonFeeCodeMapping
     */
    @Override
    public void setSysCommonFeeCodeMapping(Map<String, SysFeeCodeDTO> sysCommonFeeCodeMapping) {
        log.info("{} 保存斯维尔费用代号系统映射信息到systemCommonFeeCodeMapping", simpleName);
        this.sysCommonFeeCodeMapping = sysCommonFeeCodeMapping;
    }

    /**
     * 清楚处理器内缓存
     */
    @Override
    public void clean() {
        log.info("{} 清空斯维尔系统公用费用代号映射MapsystemCommonFeeCodeMapping", simpleName);
        sysCommonFeeCodeMapping.clear();
        sysCommonFeeCodeMapping = null;
        log.info("{} 清空斯维尔取费文件专属费用代号映射MapfeeDocFeeCodeMapping", simpleName);
        feeDocFeeCodeMapping.clear();
        feeDocFeeCodeMapping = null;
    }
}
