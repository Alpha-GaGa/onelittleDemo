package com.cost.domain.wrapper;

import com.cost.enums.FileTypeCacheKeyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @description 斯维尔费用代号处理数据
 * @Created zhangtianhao
 * @date 2023-04-18 18:52
 * @version
 */
@Data
@Accessors(chain = true)
public class SweFeeCodeWrapper{
    /**
     * 费用代号
     */
    private String feeCode;

    /**
     * 费用名称
     */
    private String feeName;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 封装数据类型(0子目 1最下层指标/清单 2清单法)
     */
    private String type;

    /**
     * 文件来源类型对应cacheKey枚举类
     */
    private FileTypeCacheKeyEnum fileTypeCacheKeyEnum;

    /**
     * 单价分析费用金额
     */
    private BigDecimal feeAmount;

    /**
     * 斯维尔指标/子目调差封装类
     */
    private SweAdjustWrapper adjustWrapper;

    /**
     * todo 这个字段待考虑
     * 该费用代号调差前价格
     */
    private BigDecimal adjustBeforePrice;

    @Override
    public String toString() {
        return "SweFeeCodeWrapper{" +
                "feeCode='" + feeCode + '\'' +
                ", feeName='" + feeName + '\'' +
                ", feeDocId=" + feeDocId +
                ", type='" + type + '\'' +
                ", fileType=" + fileTypeCacheKeyEnum.getFileType() +
                ", adjustBeforePrice=" + adjustBeforePrice +
                '}';
    }
}

