package com.cost;

import com.cost.domain.common.TreeNode;

import java.util.List;
import java.util.function.Function;

/**
 * 树形数据处理器接口
 *
 * @param <T> 待处理数据类型
 */
public interface TreeDataHandler<T extends TreeNode> {

    /**
     * 将List数据转换成树形结构
     *
     * @param pendingTreeNode 待处理List数据
     * @return 树形结构数据
     */
    default Object getTree(List<T> pendingTreeNode) {
        // 在转换树形结构前的业务处理

        // 构建树
        Object result = buildTree(pendingTreeNode, convertToTree(pendingTreeNode));

        // 在转换树形结构后的业务处理
        return result;
    }

    /**
     * 将List数据转换成树形结构
     *
     * @param pendingTreeNode 待处理List数据
     * @param convertToTree   构建树的函数接口
     * @return 树形结构数据
     */
    Object buildTree(List<T> pendingTreeNode, Function<List<T>, T> convertToTree);

    /**
     * 按parentId分组后的节点数据
     *
     * @param nodeList 树形结构节点列表
     * @return 按parentId分组后的节点数据
     */
    List<T> groupByParentId(List<T> nodeList);

    /**
     * 实际的树形结构构建逻辑
     *
     * @param pendingTreeNode 待处理节点列表
     * @return 构建后的树形结构节点列表
     */
    T convertToTree(List<T> pendingTreeNode);

    /**
     * 单个节点的后置处理逻辑
     *
     * @param node 单个节点
     */
    void postProcessNode(T node);
}