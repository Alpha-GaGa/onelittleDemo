package com.cost.handler;

import com.cost.domain.common.TreeNode;
import com.cost.domain.wrapper.SweAdjustWrapper;

import java.util.List;

/**
 * @description 单价分析处理器接口
 * @Created zhangtianhao
 * @date 2023-04-20 16:55
 * @version
 */
public interface AnalysisHandler<T extends TreeNode> {

    /**
     *
     * @param pendingTreeNode
     * @return
     */
    List<T> analysis(List<T> pendingTreeNode);

    /**
     * 设置费用代号处理器
     * @param feeCodeMatchHandler 费用代号处理器
     */
    void setFeeCodeHandler(FeeCodeMatchHandler feeCodeMatchHandler);
}
