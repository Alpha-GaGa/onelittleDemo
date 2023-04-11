package com.cost.util;

import com.googlecode.aviator.AviatorEvaluator;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Map;


/**
 * @description 计算类
 * @Created zhangtianhao
 * @date 2023-04-11 23:16
 * @version
 */
public class CalculateUtils {

    /**
     * 使用Jexl计算表达式
     * @param expr
     * @param JexlMapContext
     * @return
     */
    public static BigDecimal calculateByJexl(String expr, Map<String, BigDecimal> JexlMapContext){

        if(StringUtils.isBlank(expr)){
            throw new IllegalArgumentException("计算方表达式expr不能为空");
        }

        if(CollectionUtils.isEmpty(JexlMapContext)){
            throw new IllegalArgumentException("计算上下文JexlMapContext不能为空");
        }

        JexlEngine jexl = new JexlBuilder().create();

        MapContext mapContext = new MapContext();

        JexlMapContext.keySet().forEach(key -> mapContext.set(key, JexlMapContext.get(key)));

        JexlExpression e = jexl.createExpression(expr);

        // 计算表达式
        Object result = e.evaluate(mapContext);
        return new BigDecimal(String.valueOf(result));

    }

    /**
     * 使用Aviator计算表达式
     * @param expr
     * @param JexlMapContext
     * @return
     */
    private static BigDecimal calculateByAviator(String expr, Map<String, Object> JexlMapContext){

        if(StringUtils.isBlank(expr)){
            throw new IllegalArgumentException("计算方表达式expr不能为空");
        }

        if(CollectionUtils.isEmpty(JexlMapContext)){
            throw new IllegalArgumentException("计算上下文JexlMapContext不能为空");
        }

        return (BigDecimal) AviatorEvaluator.execute(expr, JexlMapContext);

    }
}
