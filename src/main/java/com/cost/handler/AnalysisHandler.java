package com.cost.handler;

import com.cost.domain.common.TreeNode;

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
}
