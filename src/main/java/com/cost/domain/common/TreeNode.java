package com.cost.domain.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 */
@Data
@NoArgsConstructor
public class TreeNode<T extends TreeNode> {
    /**
     * ID
     */
    private Long id;
    /**
     * 父节点ID
     */
    private Long parentId;
    /**
     * 节点Id构成的路径
     */
    private String path;
    /**
     * 子节点集合
     */
    private List<T> childList;

    public void addChild(T childNode){
        if (CollectionUtils.isEmpty(childList)){
            childList = new ArrayList<T>();
        }
        childList.add(childNode);
    }
}
