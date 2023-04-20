package com.cost.factory;

import com.cost.enums.FileTypeRedisKeyEnum;
import com.cost.handler.FeeCodeMatchHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cost.enums.FileTypeRedisKeyEnum.SWE;

/**
 * @description 单价分析处理器工厂
 * @Created zhangtianhao
 * @date 2023-04-20 16:59
 * @version
 */
@Slf4j
@Component
public class AnalysisHandlerFactory {

    @Autowired
    private FeeCodeHandlerFactory feeCodeHandlerFactory;

    public FeeCodeMatchHandler getAdjustHandler(String fileType) {
        switch (FileTypeRedisKeyEnum.getByFileType(fileType)) {
            // 处理自定义数据文件
            case SELF:
                return null;
            // 处理斯维尔文件
            case SWE:
                return initAdjustHandler(SWE);
            default:
                throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
        }
    }

    /**
     *
     * @param fileTypeRedisKeyEnum
     */
    private FeeCodeMatchHandler initAdjustHandler(FileTypeRedisKeyEnum fileTypeRedisKeyEnum) {

    }
}
