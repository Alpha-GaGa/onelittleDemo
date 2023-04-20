package com.cost.handler;

import com.cost.domain.common.TreeNode;

import java.util.List;
import java.util.Map;

/**
 * @description 树形数据构造器
 * @Created zhangtianhao
 * @date 2023-04-20 16:47
 * @version
 */
public interface TreeBuilder {

    /**
     *  将List数据转换成树形结构
     * @param pendingTreeNode 待处理List数据
     * @return 树形结构数据
     * @param <T> 待处理数据类型
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    <T extends TreeNode> List<T> getTree(List<T> pendingTreeNode) throws InstantiationException, IllegalAccessException;

    /**
     *  构件树形结构
     * @param node 当前操作节点
     * @param groupByParentId 按照parentId分组Map集合
     * @return 完成操作的节点
     * @param <T> 待操作数据类型
     */
    <T extends TreeNode> T buildTree(T node, Map<Long, List<T>> groupByParentId);
}
