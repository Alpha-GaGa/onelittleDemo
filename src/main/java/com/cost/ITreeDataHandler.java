package com.cost;


import com.cost.domain.common.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 树形数据处理器
 * @Created zhangtianhao
 * @date 2023-04-07 10:57
 * @version
 */
@Slf4j
public abstract class ITreeDataHandler {

    /**
     *  将List数据转换成树形结构
     * @param pendingTreeNode 待处理List数据
     * @return 树形结构数据
     * @param <T> 待处理数据类型
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public final <T extends TreeNode> List<T> getTree(List<T> pendingTreeNode) throws InstantiationException, IllegalAccessException {

        if(CollectionUtils.isEmpty(pendingTreeNode)){
            return null;
        }

        // 按照parentId分组
        log.info("正在按照parentId给数据进行分组，并且对节点进行前置处理，该次处理的节点数量为：{}", pendingTreeNode.size());
        Map<Long, List<T>> groupByParentId = pendingTreeNode.stream()
                .map(node -> {
                    // 对当前节点的前置业务处理
                    nodeProcessBefore(node, pendingTreeNode);
                    return node;
                })
                .collect(Collectors.groupingBy(T::getParentId));

        // 在转换树形结构后的前置业务处理
        treeProcessBefore(pendingTreeNode);

        // todo 要修改成动态代理或者别的设计模式来保证扩展性
        List<T> nodeList = groupByParentId.get(0L).stream().map(node -> buildTree(node, groupByParentId)).collect(Collectors.toList());

        // 在转换树形结构后的后置业务处理
        treeProcessbeAfter(nodeList, groupByParentId);

        return nodeList;

    }

    /**
     *  构件树形结构
     * @param node 当前操作节点
     * @param groupByParentId 按照parentId分组Map集合
     * @return 完成操作的节点
     * @param <T> 待操作数据类型
     */
    private final <T extends TreeNode> T buildTree(T node, Map<Long, List<T>> groupByParentId){
        // 从groupByParentId中获取当前节点的子节点列表
        List<T> childList = groupByParentId.get(node.getId());
        if (!CollectionUtils.isEmpty(childList)) {
            // 如果当前节点有子节点，遍历子节点，构建树形结构
            for (T childNode : childList) {
                // 递归构建子节点的子树
                buildTree(childNode, groupByParentId);
                // 将子节点添加到当前节点的子节点列表中
                node.addChildren(childNode);
            }
        }
        // 对当前节点的后置业务处理
        nodeProcessAfter(node, groupByParentId);
        return node;
    }

    /**
     * 对当前节点的前置业务处理
     * @param node 当前操作节点
     * @param pendingTreeNode 待处理List数据
     * @param <T> 待操作数据类型
     */
    public abstract <T extends TreeNode> void nodeProcessBefore(T node, List<T> pendingTreeNode);

    /**
     * 对当前节点的后置业务处理
     * @param node 当前操作节点
     * @param groupByParentId 按照parentId分组Map集合
     * @param <T> 待操作数据类型
     */
    public abstract <T extends TreeNode> void nodeProcessAfter(T node, Map<Long, List<T>> groupByParentId);

    /**
     * 在转换树形结构的前置业务处理
     * @param pendingTreeNode 待处理List数据
     * @param <T> 待操作数据类型
     */
    public abstract <T extends TreeNode> void treeProcessBefore(List<T> pendingTreeNode);


    /**
     * 在转换树形结构后的后置业务处理
     * @param nodeList 树形结构数据集合
     * @param groupByParentId 按照parentId分组Map集合
     * @param <T> 待操作数据类型
     */
    public abstract <T extends TreeNode> void treeProcessbeAfter(List<T> nodeList, Map<Long, List<T>> groupByParentId);

}
