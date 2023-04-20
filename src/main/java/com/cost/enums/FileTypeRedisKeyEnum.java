package com.cost.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 文件来源类型对应Rediskey枚举类
 * @Created zhangtianhao
 * @date 2023-04-20 17:12
 * @version
 */
@Getter
@AllArgsConstructor
public enum FileTypeRedisKeyEnum {
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
    private String redisKey;

    public static FileTypeRedisKeyEnum getByFileType(String fileType){
        for (FileTypeRedisKeyEnum item : values()) {
            if (item.getFileType().equals(fileType)) {
                return item;
            }
        }
        throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
    }
}
