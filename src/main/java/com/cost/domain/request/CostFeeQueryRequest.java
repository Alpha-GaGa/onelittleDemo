package com.cost.domain.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description 取费文件请求参数模型
 * @Created zhangtianhao
 * @date 2023-04-23 00:04
 * @version
 */
@Data
@Accessors(chain = true)
public class CostFeeQueryRequest {
    /**
     * 建设路线id
     */
    private Long lineId;

    /**
     * 造价文件id
     */
    private Long costDocId;

    /**
     * 取费文件id
     */
    private Long feeDocId;
}
