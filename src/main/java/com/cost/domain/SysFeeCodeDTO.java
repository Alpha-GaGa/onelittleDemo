package com.cost.domain;


import com.cost.domain.common.FeeCodeConditional;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description 系统费用代号映射对象
 * @Created zhangtianhao
 * @date 2023-04-18 17:34
 */
@Data
@Accessors(chain = true)
public class SysFeeCodeDTO {
    /**
     * 源文件类型，导入类型(字典关联p_file_type，0自定义数据文件、1斯维尔源文件)
     */
    private String fileType;

    /**
     * 作用范围(0子目 1最下层指标/清单 2清单法)
     */
    private String scope;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 源费用代号
     */
    private String sourceFeeCode;

    /**
     * 系统费用代号id
     */
    private Long sysFeeCodeId;

    /**
     * 系统费用代号
     */
    private String sysFeeCode;

    /**
     * json形式的List<FeeCodeConditional>字符串
     */
    private List<FeeCodeConditional> conditional;
}
