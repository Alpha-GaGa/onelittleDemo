package com.cost.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description 费用代号映射请求参数模型
 * @Created zhangtianhao
 * @date 2023-04-19 17:00
 * @version
 */
@Data
@Accessors(chain = true)
public class FeeCodeQueryRequest {
    /**
     * 源文件类型，导入类型(字典关联p_file_type，0自定义数据文件、1斯维尔源文件)
     */
    private String fileType;

    /**
     * 取费文件id
     */
    private Long feeDocId;

    /**
     * 源费用代号
     */
    private String sourceFeeCode;

    /**
     * 作用范围(0子目 1最下层指标/清单 2清单法)
     */
    private String scope;
}
