package com.cost.constant;

import com.cost.enums.FileTypeCacheKeyEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @description 调差缓存Key
 * @Created zhangtianhao
 * @date 2023-04-20 14:44
 */
@Data
public class AdjustCacheKey {

    /**
     * 系统代号
     */
    private static final String SYS_JOB = "sys_job";

    /**
     * 调差费用代号代号
     */
    private static final String ADJUST_FEECODE = "adj_feeCode";

    /**
     * 调差取费文件代号
     */
    private static final String ADJUST_COSTFEE = "adj_costFee";

    /**
     * 冒号分隔符
     */
    private static final String COLON = ":";

    /**
     * 公共系统费用代号取费文件Id标识
     */
    private static final Long COMMON_FEE_DOC_ID = 0L;

    /**
     * 字符串处理对象
     */
    private StringBuilder adjustCacheKey = new StringBuilder();

    /**
     * 取费文件Id
     */
    private Long feeDocId = 0L;

    /**
     * 文件来源类型对应cacheKey枚举类
     */
    private FileTypeCacheKeyEnum FileTypeCacheKeyEnum ;


    /**
     * 获取adjustCacheKey
     * 会先转换为String类型adjustCacheKey
     *
     * @return String类型adjustCacheKey
     */
    public String getAdjustCacheKey() {
        String adjustCacheKey = this.adjustCacheKey.toString();
        if (StringUtils.isBlank(adjustCacheKey)) {
            throw new RuntimeException("AdjustCacheKey不能为空");
        }
        return adjustCacheKey;
    }

    /**
     * 转换为String类型的RedisKey
     * @return
     */
    public String toRedisKey() {
        // 加上系统标识
        adjustCacheKey.insert(0, COLON).insert(0, SYS_JOB);
        return this.getAdjustCacheKey();
    }


    /**
     * 获取公共系统费用代号取费文件Id标识
     * @param fileTypeCacheKeyEnum 文件来源类型对应cacheKey枚举类
     * @return
     */
    public AdjustCacheKey commonFeeCodeCacheKey(FileTypeCacheKeyEnum fileTypeCacheKeyEnum) {
        if (null == fileTypeCacheKeyEnum) {
            throw new RuntimeException("生成FeeCodeRedisKey时fileTypeRedisKeyEnum和feeDocId不能为空");
        }
        return feeCodeCacheKey(fileTypeCacheKeyEnum, COMMON_FEE_DOC_ID);
    }

    /**
     * 获取费用代号缓存标识FeeCodeCacheKey
     * @param fileTypeCacheKeyEnum 文件来源类型对应cacheKey枚举类
     * @param feeDocId 取费文件Id
     * @return
     */
    public AdjustCacheKey feeCodeCacheKey(FileTypeCacheKeyEnum fileTypeCacheKeyEnum, Long feeDocId) {
        if (null == fileTypeCacheKeyEnum || null == feeDocId) {
            throw new RuntimeException("生成FeeCodeCacheKey时fileTypeRedisKeyEnum和feeDocId不能为空");
        }
        // 保存到值到成员变量
        this.feeDocId = feeDocId;
        this.FileTypeCacheKeyEnum = fileTypeCacheKeyEnum;

        // 组装cacheKey
        adjustCacheKey.append(ADJUST_FEECODE)
                .append(COLON)
                .append(fileTypeCacheKeyEnum.getCacheKey())
                .append(COLON)
                .append(feeDocId);

        return this;
    }

    /**
     * 获取取费文件缓存标识CosFeeeCacheKey
     * @param fileTypeCacheKeyEnum 文件来源类型对应cacheKey枚举类
     * @param feeDocId 取费文件Id
     * @return
     */
    public AdjustCacheKey cosFeeeCacheKey(FileTypeCacheKeyEnum fileTypeCacheKeyEnum, Long feeDocId) {
        if (null == fileTypeCacheKeyEnum || null == feeDocId) {
            throw new RuntimeException("生成CosFeeeCacheKey时fileTypeRedisKeyEnum和feeDocId不能为空");
        }
        // 保存到值到成员变量
        this.feeDocId = feeDocId;
        this.FileTypeCacheKeyEnum = fileTypeCacheKeyEnum;

        // 组装cacheKey
        adjustCacheKey.append(ADJUST_COSTFEE)
                .append(COLON)
                .append(fileTypeCacheKeyEnum.getCacheKey())
                .append(COLON)
                .append(feeDocId);

        return this;
    }

}
