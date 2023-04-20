package com.cost.factory;

import com.cost.common.redis.service.RedisService;
import com.cost.constant.FeeCodeRedisKey;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.request.FeeCodeQueryRequest;
import com.cost.enums.FileTypeRedisKeyEnum;
import com.cost.handler.FeeCodeMatchHandler;
import com.cost.constant.FileTypeConstant;
import com.cost.handler.SweFeeCodeMatchHandler;
import com.cost.service.IFeeCodeRelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cost.enums.FileTypeRedisKeyEnum.SWE;

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

    @Autowired
    private SweFeeCodeMatchHandler sweFeeCodeMatchHandler;

    /**
     * 获取费用代号处理器
     * @param fileType 文件来源类型常量类
     * @return 费用代号处理器
     */
    public FeeCodeMatchHandler getFeeCodeHandler(String fileType) {
        switch (FileTypeRedisKeyEnum.getByFileType(fileType)) {
            // 处理自定义数据文件
            case SELF:
                return null;
            // 处理斯维尔文件
            case SWE:
                return initFeeCodeHandler(sweFeeCodeMatchHandler, SWE);
            default:
                throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
        }
    }

    /**
     * 初始化费用代号处理器
     * @param feeCodeMatchHandler 待初始化费用代号处理器
     * @param fileTypeRedisKeyEnum 文件来源类型对应Rediskey枚举类
     * @return 费用代号处理器
     */
    private SweFeeCodeMatchHandler initFeeCodeHandler(FeeCodeMatchHandler feeCodeMatchHandler, FileTypeRedisKeyEnum fileTypeRedisKeyEnum) {
        log.info("正在初始化{}费用代号处理器{}", fileTypeRedisKeyEnum.getRedisKey(), feeCodeMatchHandler.getClass().getSimpleName());
        // todo 目前这个FeeCodeHandler线程并不安全，如果一个服务实例同时计算一套相同来源的造价文件会造成问题。要不就删除

        // 清楚处理器内缓存数据
        feeCodeMatchHandler.clean();

        // 组装redisKey
        String sysComminFeeCodeRedisKey = FeeCodeRedisKey.getCommonFeeCodeRedisKey(fileTypeRedisKeyEnum);

        Map<String, SysFeeCodeDTO> sysCommonFeeCodeMapping = null;

        if (Boolean.TRUE.equals(redisService.hasKey(sysComminFeeCodeRedisKey))) {
            // 从缓存中获取
            log.info("从Reids获取斯维尔费用代号系统映射信息 redisKey{} outTime {}", sysComminFeeCodeRedisKey, redisService.getExpire(sysComminFeeCodeRedisKey));
            sysCommonFeeCodeMapping = redisService.getCacheObject(sysComminFeeCodeRedisKey);
        } else {
            // 从数据库中获取
            log.info("从数据库获取斯维尔费用代号系统映射信息");
            List<SysFeeCodeDTO> sysFeeCodeDTOList = feeCodeRelService.selectFeeCode(
                    new FeeCodeQueryRequest()
                            .setFeeDocId(0L)
                            .setFileType(FileTypeConstant.SWE_FILE)
            );

            // 处理数据，sourceFeeCode为key保存到HashMap中
            sysCommonFeeCodeMapping = sysFeeCodeDTOList.stream()
                    .collect(Collectors.toMap(SysFeeCodeDTO::getSourceFeeCode, sysFeeCodeDTO -> sysFeeCodeDTO));

            log.info("保存斯维尔费用代号系统映射信息到Redis redisKey{} outTime {}{}", sysComminFeeCodeRedisKey, FeeCodeRedisKey.EXPIRATION, TimeUnit.MINUTES);
            redisService.setCacheObject(sysComminFeeCodeRedisKey, sysCommonFeeCodeMapping, FeeCodeRedisKey.EXPIRATION, TimeUnit.MINUTES);
        }

        // 给斯维尔费用代号处理器设置系统映射
        sweFeeCodeMatchHandler.setSysCommonFeeCodeMapping(sysCommonFeeCodeMapping);
        return sweFeeCodeMatchHandler;
    }
}
