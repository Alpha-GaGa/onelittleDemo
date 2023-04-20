package com.cost.constant;

import com.cost.enums.FileTypeRedisKeyEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @description 费用代号相关Rediskey
 * @Created zhangtianhao
 * @date 2023-04-20 14:44
 */
public interface FeeCodeRedisKey {

    /**
     * 未用代号
     */
    String SYS_JOB_ADJUST_FEECODE = "sys_job:adjust_feeCode";

    /**
     * 冒号分隔符
     */
    String COLON = ":";

    /**
     * 公共系统费用代号取费文件Id标识
     */
    Long COMMON_FEE_DOC_ID = 0L;

    /**
     * 缓存有效期，默认5（分钟）
     */
    long EXPIRATION = 5;


    public static String getCommonFeeCodeRedisKey(FileTypeRedisKeyEnum fileTypeRedisKeyEnum) {

        if (null == fileTypeRedisKeyEnum) {
            throw new RuntimeException("生成FeeCodeRedisKey时fileTypeRedisKeyEnum和feeDocId不能为空");
        }
        return getFeeCodeRedisKey(fileTypeRedisKeyEnum, COMMON_FEE_DOC_ID);
    }

    /**
     * 获取FeeCodeRedisKey
     * @param fileTypeRedisKeyEnum
     * @param feeDocId
     * @return
     */
    public static String getFeeCodeRedisKey(FileTypeRedisKeyEnum fileTypeRedisKeyEnum, Long feeDocId) {

        if (null == fileTypeRedisKeyEnum || null == feeDocId) {
            throw new RuntimeException("生成FeeCodeRedisKey时fileTypeRedisKeyEnum和feeDocId不能为空");
        }
        return new StringBuffer(SYS_JOB_ADJUST_FEECODE)
                .append(COLON)
                .append(fileTypeRedisKeyEnum.getRedisKey())
                .append(COLON)
                .append(feeDocId)
                .toString();
    }
}
