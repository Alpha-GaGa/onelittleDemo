package com.cost.factory;

import com.cost.enums.FileTypeCacheKeyEnum;
import com.cost.handler.FeeCodeMatchHandler;
import com.cost.handler.SweCostFeeMatchHandler;
import com.cost.handler.SweFeeCodeMatchHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @description 费用代号处理器工厂类
 * @Created zhangtianhao
 * @date 2023-04-20 00:09
 */
@Slf4j
@Component
public class FeeCodeHandlerFactory {

    @Autowired
    private SweFeeCodeMatchHandler sweFeeCodeMatchHandler;

    @Autowired
    private SweCostFeeMatchHandler sweCostFeeMatchHandler;

    /**
     * 获取费用代号处理器
     * @param fileType 文件来源类型常量类
     * @return 费用代号处理器
     */
    public FeeCodeMatchHandler getFeeCodeHandler(String fileType) {
        switch (FileTypeCacheKeyEnum.getByFileType(fileType)) {
            // 处理自定义数据文件
            case SELF:
                return null;
            // 处理斯维尔文件
            case SWE:
                return sweFeeCodeMatchHandler;
            default:
                throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
        }
    }

    /**
     * 获取取费文件处理器
     * @param fileType 文件来源类型常量类
     * @return 取费文件处理器
     */
    public FeeCodeMatchHandler getCostFeeHandler(String fileType) {
        switch (FileTypeCacheKeyEnum.getByFileType(fileType)) {
            // 处理斯维尔文件
            case SWE:
                return sweCostFeeMatchHandler;
            default:
                throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
        }
    }
}
