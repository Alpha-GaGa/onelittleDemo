package com.cost.domain.request;

/**
 * @description 取费文件请求参数模型
 * @Created zhangtianhao
 * @date 2023-04-23 00:04
 * @version
 */
public class CostFeeQueryRequest {
    /**
     * 源文件类型，导入类型(字典关联p_file_type，0自定义数据文件、1斯维尔源文件)
     */
    private String fileType;

    /**
     * 取费文件id
     */
    private Long feeDocId;
}
