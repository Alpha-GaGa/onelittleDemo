package com.cost.factory;

import com.cost.constant.FeeCodeScopeConstant;
import com.cost.domain.wrapper.SweAdjustWrapper;
import com.cost.enums.FileTypeCacheKeyEnum;
import com.cost.handler.AnalysisHandler;
import com.cost.handler.SweAnalysisHandler;
import com.cost.handler.SweIndexAnalysisHandler;
import com.cost.handler.SweItemAnalysisHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @description 单价分析处理器工厂
 * @Created zhangtianhao
 * @date 2023-04-20 16:59
 */
@Slf4j
@Component
public class AnalysisHandlerFactory {

    @Autowired
    private FeeCodeHandlerFactory feeCodeHandlerFactory;

    /**
     * @param adjustWrapper
     * @param fileType
     * @param feeCodeScope
     * @return
     */
    public AnalysisHandler getAnalysisHandler(SweAdjustWrapper adjustWrapper, String fileType, String feeCodeScope) {
        switch (FileTypeCacheKeyEnum.getByFileType(fileType)) {
            // 处理自定义数据文件
            case SELF:
                return null;
            // 处理斯维尔文件
            case SWE:
                return getSweAnalysisHandler(adjustWrapper, feeCodeScope);
            default:
                throw new RuntimeException("暂不支持解析fileType为" + fileType + "类型的数据");
        }
    }


    /**
     * @param adjustWrapper
     * @param feeCodeScope
     * @return
     */
    private SweAnalysisHandler getSweAnalysisHandler(SweAdjustWrapper adjustWrapper, String feeCodeScope) {
        // 创建对应的单价分析处理器
        switch (feeCodeScope) {
            // 子目
            case FeeCodeScopeConstant.ITEM:
                return initSweAnalysisHandler(
                        adjustWrapper,
                        SweItemAnalysisHandler.class
                );
            // 最下层指标/清单
            case FeeCodeScopeConstant.INDEX:
                SweIndexAnalysisHandler analysisHandler = initSweAnalysisHandler(
                        adjustWrapper,
                        SweIndexAnalysisHandler.class
                );
                // 设置取费文件处理器
                analysisHandler.setSweCostFeeMatchHandler(feeCodeHandlerFactory.getCostFeeHandler(FileTypeCacheKeyEnum.SWE.getFileType()));
                return analysisHandler;

            // 清单法 todo 需要等产品澄清
            case FeeCodeScopeConstant.INVENTORY:
                return null;
            default:
                throw new RuntimeException("暂不支持解析 feeCodeScope=" + feeCodeScope + " 类型的数据");
        }
    }

    /**
     * 初始化斯维尔单价分析处理器
     *
     * @param adjustWrapper
     * @param handlerClass
     * @param <T>
     * @return
     */
    private <T extends SweAnalysisHandler> T initSweAnalysisHandler(SweAdjustWrapper adjustWrapper, Class<T> handlerClass) {
        // 获取实例方法2
        T sweAnalysisHandler = null;
        try {
            sweAnalysisHandler = handlerClass.newInstance();
        } catch (Exception e) {
            // todo 需要做异常处理
            throw new RuntimeException("初始化斯维尔单价分析处理器失败");
        }
        // 设置文件来源类型
        sweAnalysisHandler.setFileTypeCacheKeyEnum(FileTypeCacheKeyEnum.SWE);
        // 设置取费文件Id
        sweAnalysisHandler.setFeeDocId(adjustWrapper.getFeeDocId());
        // 设置斯维尔指标/子目调差封装类
        sweAnalysisHandler.setAdjustWrapper(adjustWrapper);
        // 设置取费代号处理器
        sweAnalysisHandler.setFeeCodeMatchHandler(feeCodeHandlerFactory.getFeeCodeHandler(FileTypeCacheKeyEnum.SWE.getFileType()));

        return sweAnalysisHandler;
    }
}
