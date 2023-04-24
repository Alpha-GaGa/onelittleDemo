package com.cost.util;

import com.googlecode.aviator.AviatorEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * @description 计算类
 * @Created zhangtianhao
 * @date 2023-04-11 23:16
 * @version
 */
@Slf4j
public class CalculateUtils {

    /**
     * 使用Jexl计算表达式
     * @param expr
     * @param jexlContext
     * @return
     */
    public static BigDecimal calculateByJexl(String expr, Map<String, BigDecimal> jexlContext){

        if(StringUtils.isBlank(expr)){
            throw new IllegalArgumentException("计算方表达式expr不能为空");
        }

        if(CollectionUtils.isEmpty(jexlContext)){
            throw new IllegalArgumentException("计算上下文JexlMapContext不能为空");
        }

        JexlEngine jexl = new JexlBuilder().create();

        MapContext mapContext = new MapContext();

        jexlContext.keySet().forEach(key -> mapContext.set(key, jexlContext.get(key)));

        JexlExpression e = jexl.createExpression(expr);

        // 计算表达式
        Object result = e.evaluate(mapContext);
        return new BigDecimal(String.valueOf(result));

    }

    /**
     * 使用Aviator计算表达式
     * @param expr
     * @param aviatorContext
     * @return
     */
    public static BigDecimal calculateByAviator(String expr, Map<String, BigDecimal> aviatorContext){

        if(StringUtils.isBlank(expr)){
            throw new IllegalArgumentException("计算方表达式expr不能为空");
        }

        if(CollectionUtils.isEmpty(aviatorContext)){
            throw new IllegalArgumentException("计算上下文JexlMapContext不能为空");
        }

        log.info("计算的方程式为:{}", expr);
        log.info("提供的基础数据类:{}", aviatorContext);

        // 创建上下文并设置变量
        Map<String, Object> context = new HashMap<>();
        context.putAll(aviatorContext);
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(AviatorEvaluator.execute(expr, context)));

        log.info("计算结果为:{}", bigDecimal);
        return  bigDecimal;

    }




    //// 将 Aviator 编译后的表达式缓存起来
    //private static final Map<String, com.googlecode.aviator.Expression> AVIATOR_EXPRESSION_CACHE = new HashMap<>();
    //// 创建 JEXL 引擎和上下文
    //private static final JexlEngine JEXL_ENGINE = new JexlEngine();
    //private static final ThreadLocal<MapContext> JEXL_CONTEXT = ThreadLocal.withInitial(MapContext::new);
    //
    ///**
    // * 使用 Aviator 计算表达式
    // *
    // * @param expr 要计算的表达式
    // * @param contextMap 基础数据
    // * @return 计算结果
    // */
    //public static BigDecimal calculateWithAviator(String expr, Map<String, BigDecimal> contextMap) {
    //    // 从缓存中获取已编译的表达式，如果不存在则进行编译
    //    com.googlecode.aviator.Expression compiledExpr = AVIATOR_EXPRESSION_CACHE.computeIfAbsent(expr, k -> AviatorEvaluator.compile(expr));
    //    // 创建上下文并设置变量
    //    Map<String, Object> context = new HashMap<>();
    //    context.putAll(contextMap);
    //    // 执行表达式并返回结果
    //    return (BigDecimal) compiledExpr.execute(context);
    //}
    //
    ///**
    // * 使用 JEXL 计算表达式
    // *
    // * @param expr 要计算的表达式
    // * @param contextMap 基础数据
    // * @return 计算结果
    // */
    //public static BigDecimal calculateWithJexl(String expr, Map<String, BigDecimal> contextMap) {
    //    // 创建上下文并设置变量
    //    MapContext context = JEXL_CONTEXT.get();
    //    context.clear();
    //    contextMap.forEach(context::set);
    //    // 执行表达式并返回结果
    //    Object result = JEXL_ENGINE.createExpression(expr).evaluate(context);
    //    return new BigDecimal(result.toString());
    //}

}
