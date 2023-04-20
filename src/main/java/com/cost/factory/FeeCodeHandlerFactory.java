package com.cost.factory;

import com.cost.common.redis.service.RedisService;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.request.FeeCodeQueryRequest;
import com.cost.handler.FeeCodeHandler;
import com.cost.constant.FileTypeConstant;
import com.cost.handler.SweFeeCodeHandler;
import com.cost.service.IFeeCodeRelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description 费用代号处理器工厂类
 * @Created zhangtianhao
 * @date 2023-04-20 00:09
 */
@Slf4j
@Component
public class FeeCodeHandlerFactory {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IFeeCodeRelService feeCodeRelService;

    /**
     * 取费文件
     */
    private final static String SYS_JOB_ADJUST_FEECODE = "sys_job:adjust_feeCode";

    /**
     * 斯维尔标识
     */
    private final static String SWE = "_swe";

    /**
     * 公共系统费用代号取费文件Id标识
     */
    private final static String COMMON_FEE_DOC_ID = "_0";

    /**
     * 缓存有效期，默认5（分钟）
     */
    public final static long EXPIRATION = 5;

    @Autowired
    private SweFeeCodeHandler sweFeeCodeHandler;

    public FeeCodeHandler getFeeCodeHandler(String fileType) {
        switch (fileType) {
            // 处理自定义数据文件
            case FileTypeConstant.SELF_FILE:
                return null;
            // 处理斯维尔文件
            case FileTypeConstant.SWE_FILE:
                return getSweFeeCodeHandler();
            default:
                throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
        }
    }

    /**
     * 初始化斯维尔费用代号处理器
     *
     * @return
     */
    private SweFeeCodeHandler getSweFeeCodeHandler() {
        // 清楚处理器内缓存数据
        sweFeeCodeHandler.clean();

        // 组装redisKey
        String sweSysFeeCodeRedisKey = new StringBuffer(SYS_JOB_ADJUST_FEECODE)
                .append(SWE)
                .append(COMMON_FEE_DOC_ID)
                .toString();

        Map<String, SysFeeCodeDTO> systemCommonFeeCodeMapping = null;

        if (Boolean.TRUE.equals(redisService.hasKey(sweSysFeeCodeRedisKey))) {
            // 从缓存中获取
            log.info("正在从Reids获取斯维尔费用代号系统映射信息 redisKey{} outTime {}", sweSysFeeCodeRedisKey, redisService.getExpire(sweSysFeeCodeRedisKey));
            systemCommonFeeCodeMapping = redisService.getCacheObject(sweSysFeeCodeRedisKey);
        } else {
            // 从数据库中获取
            log.info("正在从数据库获取斯维尔费用代号系统映射信息");
            List<SysFeeCodeDTO> sysFeeCodeDTOList = feeCodeRelService.selectFeeCode(
                    new FeeCodeQueryRequest()
                            .setFeeDocId(0L)
                            .setFileType(FileTypeConstant.SWE_FILE)
            );

            // 处理数据，sourceFeeCode为key保存到HashMap中
            systemCommonFeeCodeMapping = sysFeeCodeDTOList.stream()
                    .collect(Collectors.toMap(SysFeeCodeDTO::getSourceFeeCode, sysFeeCodeDTO -> sysFeeCodeDTO));

            log.info("保存斯维尔费用代号系统映射信息到Redis redisKey{} outTime {}{}", sweSysFeeCodeRedisKey, EXPIRATION, TimeUnit.MINUTES);
            redisService.setCacheObject(sweSysFeeCodeRedisKey, systemCommonFeeCodeMapping, EXPIRATION, TimeUnit.MINUTES);
        }

        // 给斯维尔费用代号处理器设置系统映射
        sweFeeCodeHandler.setSystemCommonFeeCodeMapping(systemCommonFeeCodeMapping);
        return sweFeeCodeHandler;
    }
}
