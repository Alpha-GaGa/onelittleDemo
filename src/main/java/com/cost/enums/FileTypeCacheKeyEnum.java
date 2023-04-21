package com.cost.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 文件来源类型对应cacheKey枚举类
 * @Created zhangtianhao
 * @date 2023-04-20 17:12
 * @version
 */
@Getter
@AllArgsConstructor
public enum FileTypeCacheKeyEnum {
    /**
     * 自定义数据文件
     */
    SELF("0", "self"),

    /**
     * 斯维尔源文件
     */
    SWE("1", "swe");

    /**
     * 文件来源类型
     */
    private String fileType;

    /**
     * 文件来源类型对应Rediskey
     */
    private String cacheKey;

    public static FileTypeCacheKeyEnum getByFileType(String fileType){
        for (FileTypeCacheKeyEnum item : values()) {
            if (item.getFileType().equals(fileType)) {
                return item;
            }
        }
        throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
    }
}
