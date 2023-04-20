package com.cost.domain.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 构件树形对象接口
 * @Created zhangtianhao
 * @date 2023-04-12 00:18
 * @version
 */
public interface TreeNode<T extends TreeNode<T>> {
    /**
     * 获取自身Id
     * @return
     */
    Long getId();

    /**
     * 获取父节点If
     * @return
     */
    Long getParentId();

    /**
     * 获取子目录
     * @return
     */
    List<T> getChildren();

    /**
     * 获取子目录
     * @return
     */
    void setChildren(List<T> children);

    /**
     * 默认的 addChild 实现方法
     * @param childNode 子节点
     */
    default void addChildren(T childNode) {
        // 如果子节点没空，直接返回
        if (childNode == null) {
            return;
        }

        // 如果子节点集合为空，创建新集合
        if (getChildren() == null) {
            setChildren(new ArrayList<>());
        }

        // 添加子节点
        getChildren().add(childNode);
    }
}