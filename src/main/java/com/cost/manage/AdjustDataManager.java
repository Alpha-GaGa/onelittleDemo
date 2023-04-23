package com.cost.manage;

import com.cost.common.redis.service.RedisService;
import com.cost.constant.AdjustCacheKey;
import com.cost.domain.CostFee;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.request.CostFeeQueryRequest;
import com.cost.domain.request.FeeCodeQueryRequest;
import com.cost.service.ICostFeeService;
import com.cost.service.IFeeCodeRelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description 调差数据管理中心
 * @Created zhangtianhao
 * @date 2023-04-20 22:33
 */
@Slf4j
@Component
@CacheConfig(cacheNames = "adjustCacheManager")
public class AdjustDataManager {


    @Autowired
    private RedisService redisService;

    @Autowired
    private IFeeCodeRelService feeCodeRelService;

    @Autowired
    private ICostFeeService costFeeService;

    /**
     * 缓存有效期，默认5（分钟）
     */
    private static final Long EXPIRATION = 5L;


    /**
     * 获取费用代号映射Map
     * 先查询Caffeine本地缓存 （有数据直接返回）
     * 无数据，查询Redis分布式缓存 （有数据，缓存到Caffeine再返回）
     * 无数据，查询Mysql关系型数据库 （有数据，缓存到Redis和Caffeine再返回）
     *
     * @param adjustCacheKey 调差缓存Key
     * @return 费用代号映射Map
     */
    @Cacheable(key = "#adjustCacheKey.adjustCacheKey")
    public Map<String, SysFeeCodeDTO> getFeeCodeMapping(AdjustCacheKey adjustCacheKey) {
        // 获取标识对应的Rediskey
        String adjustRedisKey = adjustCacheKey.toRedisKey();
        Map<String, SysFeeCodeDTO> feeCodeMapping = null;
        if (Boolean.TRUE.equals(redisService.hasKey(adjustRedisKey))) {
            // 从缓存中获取
            log.info("正在从Reids获取 fileType={} 、 feeDocId={}  的专属费用代号对应规则 redisKey={} outTime={}",
                    adjustCacheKey.getFileTypeCacheKeyEnum().getFileType(),
                    adjustCacheKey.getFeeDocId(),
                    adjustRedisKey,
                    redisService.getExpire(adjustRedisKey));
            feeCodeMapping = redisService.getCacheObject(adjustRedisKey);
        } else {
            // 从数据库中获取
            log.info("正在从数据库获取 fileType={} 、 feeDocId={} 的专属费用代号对应规则",
                    adjustCacheKey.getFileTypeCacheKeyEnum().getFileType(),
                    adjustCacheKey.getFeeDocId());
            List<SysFeeCodeDTO> feeCodeDTOList = feeCodeRelService.selectFeeCode(
                    new FeeCodeQueryRequest()
                            .setFeeDocId(adjustCacheKey.getFeeDocId())
                            .setFileType(adjustCacheKey.getFileTypeCacheKeyEnum().getFileType())
            );

            // 以sourceFeeCode为key处理数据
            feeCodeMapping = feeCodeDTOList.stream().collect(Collectors.toMap(SysFeeCodeDTO::getSourceFeeCode, sysFeeCodeDTO -> sysFeeCodeDTO));

            log.info("保存 fileType={} 、  feeDocId={} 的专属费用代号对应规则到Redis redisKey={} outTime={}{}",
                    adjustCacheKey.getFileTypeCacheKeyEnum().getFileType(),
                    adjustCacheKey.getFeeDocId(),
                    adjustRedisKey,
                    EXPIRATION,
                    TimeUnit.MINUTES);

            // 保存到Redis中
            redisService.setCacheObject(adjustRedisKey, feeCodeMapping, EXPIRATION, TimeUnit.MINUTES);
        }
        return feeCodeMapping;
    }

    /**
     * 获取取费文件
     * 先查询Caffeine本地缓存 （有数据直接返回）
     * 无数据，查询Redis分布式缓存 （有数据，缓存到Caffeine再返回）
     * 无数据，查询Mysql关系型数据库 （有数据，缓存到Redis和Caffeine再返回）
     *
     * @param adjustCacheKey 调差缓存Key
     * @return 费用代号映射Map
     */
    @Cacheable(key = "#adjustCacheKey.adjustCacheKey")
    public Map<String, CostFee> getCostFeeMapping(AdjustCacheKey adjustCacheKey) {
        String fileType = adjustCacheKey.getFileTypeCacheKeyEnum().getFileType();
        Long feeDocId = adjustCacheKey.getFeeDocId();

        // 从数据库中获取
        log.info("正在从数据库获取 fileType={} 、 feeDocId={} 的取费文件", fileType, feeDocId);
        // todo 查询条件需要重新设置，走索引
        List<CostFee> costFeeList = costFeeService.selectCostFee(
                new CostFeeQueryRequest()
        );

        // 处理数据，feeCode为key保存到HashMap中
        return costFeeList.stream().collect(Collectors.toMap(CostFee::getFeeCode, costFee -> costFee));
    }


}
