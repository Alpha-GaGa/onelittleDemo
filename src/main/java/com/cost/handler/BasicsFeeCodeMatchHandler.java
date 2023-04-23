package com.cost.handler;

import com.cost.constant.FeeCodeConditionalActionOnConstant;
import com.cost.constant.FeeCodeConditionalJudgeConstant;
import com.cost.constant.FeeCodeConditionalTypeConstant;
import com.cost.constant.FeeCodeScopeConstant;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.common.FeeCodeConditional;
import com.cost.domain.wrapper.SweAdjustWrapper;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.util.CalculateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description 基础费用代号分析处理器接口
 * @Created zhangtianhao
 * @date 2023-04-20 18:35
 */
public abstract class BasicsFeeCodeMatchHandler implements FeeCodeMatchHandler {

    /**
     * 筛选器
     */
    public static final Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");


    /**
     * 节点的费用代号分析
     *
     * @param feeCodeWrapper 需要解析的对象
     * @param sysFeeCodeDTO 系统费用代号映射对象
     * @return 返回分析出来的结果
     */
    @Override
    public BigDecimal analysis(SweFeeCodeWrapper feeCodeWrapper, SysFeeCodeDTO sysFeeCodeDTO) {
        // 获取规则
        List<FeeCodeConditional> conditional = Optional.ofNullable(sysFeeCodeDTO)
                .map(SysFeeCodeDTO::getConditional)
                .orElseThrow(() ->
                        // todo 需要加异常
                        new RuntimeException("feeCode=" + feeCodeWrapper.getFeeCode() + " 没有对应的SysFeeCodeDTO规则"));

        BigDecimal result = BigDecimal.ZERO;
        // 按照规则计算
        conditional.stream().sorted(Comparator.comparing(FeeCodeConditional::getOrder)).forEach(feeCodeConditional -> {
            switch (feeCodeConditional.getType()) {
                // 直接取值
                case FeeCodeConditionalTypeConstant.DIRECT_VALUE:
                    // 判断是否只作用于自身
                    if (!FeeCodeConditionalActionOnConstant.SELF.equals(feeCodeConditional.getActionOn())) {
                        // todo 需要加异常
                        throw new RuntimeException("直接取值费用代号只允许取自身的值，actionOn=1");
                    }
                    result.add(directValue(
                            feeCodeWrapper.getAdjustWrapper(),
                            FeeCodeConditionalActionOnConstant.SELF,
                            feeCodeConditional.getConditionalExpr())
                    );
                    break;
                // 计算取值
                case FeeCodeConditionalTypeConstant.CALCULATE:
                    // 判断是否只作用于自身
                    if (!FeeCodeConditionalActionOnConstant.SELF.equals(feeCodeConditional.getActionOn())) {
                        // todo 需要加异常
                        throw new RuntimeException("计算获取费用代号只允许取自身的值，actionOn=1");
                    }
                    result.add(calculate(
                            feeCodeWrapper.getAdjustWrapper(),
                            FeeCodeConditionalActionOnConstant.SELF,
                            feeCodeConditional.getConditionalExpr())
                    );
                    break;
                // 筛选累加
                case FeeCodeConditionalTypeConstant.FILTERED_ACCUMULATION:
                    result.add(filteredAccumulation(feeCodeWrapper, feeCodeConditional));
                    break;
                // 筛选剔除，todo 应该没有使用场景
                case FeeCodeConditionalTypeConstant.FILTERED_ELIMINATE:
                    break;
                // 无法匹配，直接报错
                default:
                    throw new RuntimeException("费用代号关系对象类型 FeeCodeConditional.type=" + feeCodeConditional.getType() + " 无法解析");
            }

        });
        return result;
    }

    /**
     * 通过筛选累加取值
     *
     * @param feeCodeWrapper     待处理对象
     * @param feeCodeConditional 费用代号条件对象
     * @return 计算值
     */
    private BigDecimal filteredAccumulation(SweFeeCodeWrapper feeCodeWrapper, FeeCodeConditional feeCodeConditional) {
        // 分析作用范围
        List<SweAdjustWrapper> adjustWrapperList = actionOn(feeCodeWrapper, feeCodeConditional);

        // 判空
        if (CollectionUtils.isEmpty(adjustWrapperList)) {
            return null;
        }

        switch (feeCodeConditional.getJudgementCondition()) {
            // 条件为相等
            case FeeCodeConditionalJudgeConstant.EQUAL:
                return equals(adjustWrapperList, feeCodeWrapper.getType(), feeCodeConditional);
            // 条件为不相等，todo 应该没有使用场景
            case FeeCodeConditionalJudgeConstant.UNEQUAL:
                return null;
            // 条件为相等
            case FeeCodeConditionalJudgeConstant.GREATER_THAN:
                return greaterThan(adjustWrapperList, feeCodeWrapper.getType(), feeCodeConditional);
            // 条件为相等
            case FeeCodeConditionalJudgeConstant.LESS_THAN:
                return lessThan(adjustWrapperList, feeCodeWrapper.getType(), feeCodeConditional);
            // 条件为相等
            case FeeCodeConditionalJudgeConstant.CONTAIN:
                return contain(adjustWrapperList, feeCodeWrapper.getType(), feeCodeConditional);
            // 无法匹配，直接报错
            // todo 需要异常处理
            default:
                throw new RuntimeException("费用代号关系对象类型 FeeCodeConditional.judgementCondition=" + feeCodeConditional.getJudgementCondition() + " 无法解析");

        }
    }

    /**
     * 筛选条件为equals时执行逻辑
     *
     * @param adjustWrapperlist  待处理对象集合
     * @param type               封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param feeCodeConditional 取费代号条件对象
     * @return 计算值
     */
    private BigDecimal equals(List<SweAdjustWrapper> adjustWrapperlist, String type, FeeCodeConditional feeCodeConditional) {
        BigDecimal result = BigDecimal.ZERO;
        // 遍历待处理对象集合adjustWrapperlist
        adjustWrapperlist.forEach(adjustWrapper -> {
                    // todo 如果判断的是合价呢？
                    // 如果集合内adjustWrapper对象JudgementField字段value与JudgementValue()相同，分析conditionalExpr()，累加
                    String value = String.valueOf(getValue(adjustWrapper, feeCodeConditional.getJudgementField()));
                    if (StringUtils.isNotBlank(value) && value.equals(feeCodeConditional.getJudgementValue())) {
                        result.add(calculate(adjustWrapper, type, feeCodeConditional.getConditionalExpr()));
                    }
                }

        );
        return result;
    }

    /**
     * 筛选条件为greaterThan时执行逻辑
     *
     * @param adjustWrapperlist  待处理对象集合
     * @param type               封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param feeCodeConditional 取费代号条件对象
     * @return 计算值
     */
    private BigDecimal greaterThan(List<SweAdjustWrapper> adjustWrapperlist, String type, FeeCodeConditional feeCodeConditional) {
        BigDecimal result = BigDecimal.ZERO;
        // 遍历待处理对象集合adjustWrapperlist
        adjustWrapperlist.forEach(adjustWrapper -> {
                    // todo 如果判断的是合价呢？
                    // 获取判断字段对应的值
                    String valueStr = String.valueOf(getValue(adjustWrapper, feeCodeConditional.getJudgementField()));
                    BigDecimal value = null;
                    BigDecimal judgementValue = null;
                    // 判断字段对应值和判断值进行转换
                    try {
                        value = new BigDecimal(valueStr);
                        judgementValue = new BigDecimal(feeCodeConditional.getJudgementValue());
                    } catch (Exception e) {
                        // todo 需要加异常
                        throw new RuntimeException("judgementValue=" + feeCodeConditional.getJudgementValue() + "、 judgementFieldValue=" + valueStr + "在judgementCondition=2 时无法被解析");
                    }
                    // 如果 判断值 < 判断字段对应值 ，分析conditionalExpr()，累加
                    if (-1 == judgementValue.compareTo(value)) {
                        result.add(calculate(adjustWrapper, type, feeCodeConditional.getConditionalExpr()));
                    }
                }
        );
        return result;
    }

    /**
     * 筛选条件为lessThan时执行逻辑
     *
     * @param adjustWrapperlist  待处理对象集合
     * @param type               封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param feeCodeConditional 取费代号条件对象
     * @return 计算值
     */
    private BigDecimal lessThan(List<SweAdjustWrapper> adjustWrapperlist, String type, FeeCodeConditional feeCodeConditional) {
        BigDecimal result = BigDecimal.ZERO;
        // 遍历待处理对象集合adjustWrapperlist
        adjustWrapperlist.forEach(adjustWrapper -> {
                    // todo 如果判断的是合价呢？
                    // 获取判断字段对应的值
                    String valueStr = String.valueOf(getValue(adjustWrapper, feeCodeConditional.getJudgementField()));
                    BigDecimal value = null;
                    BigDecimal judgementValue = null;
                    // 判断字段对应值和判断值进行转换
                    try {
                        value = new BigDecimal(valueStr);
                        judgementValue = new BigDecimal(feeCodeConditional.getJudgementValue());
                    } catch (Exception e) {
                        // todo 需要加异常
                        throw new RuntimeException("judgementValue=" + feeCodeConditional.getJudgementValue() + "、 judgementFieldValue=" + valueStr + "在judgementCondition=2 时无法被解析");
                    }
                    // 如果 判断值 > 判断字段对应值 ，分析conditionalExpr()，累加
                    if (1 == judgementValue.compareTo(value)) {
                        result.add(calculate(adjustWrapper, type, feeCodeConditional.getConditionalExpr()));
                    }
                }
        );
        return result;
    }

    /**
     * 筛选条件为contain时执行逻辑
     *
     * @param adjustWrapperlist  待处理对象集合
     * @param type               封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param feeCodeConditional 取费代号条件对象
     * @return 计算值
     */
    private BigDecimal contain(List<SweAdjustWrapper> adjustWrapperlist, String type, FeeCodeConditional feeCodeConditional) {
        BigDecimal result = BigDecimal.ZERO;
        // 遍历待处理对象集合adjustWrapperlist
        adjustWrapperlist.forEach(adjustWrapper -> {
                    // todo 如果判断的是合价呢？
                    // 如果集合内adjustWrapper对象JudgementField字段value包含JudgementValue()，分析conditionalExpr()，累加
                    String value = String.valueOf(getValue(adjustWrapper, feeCodeConditional.getJudgementField()));
                    if (StringUtils.isNotBlank(value) && value.contains(feeCodeConditional.getJudgementValue())) {
                        result.add(calculate(adjustWrapper, type, feeCodeConditional.getConditionalExpr()));
                    }
                }
        );
        return result;
    }

    /**
     * 通过计算获取值
     *
     * @param adjustWrapper 待处理对象
     * @param type          封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param expr          计算表达式
     * @return 计算值
     */
    private BigDecimal calculate(SweAdjustWrapper adjustWrapper, String type, String expr) {
        // 判断计算获取值的conditionalExpr格式是否正确
        if (StringUtils.isBlank(expr) || !expr.matches("[a-zA-Z_+\\-*/()]+")) {
            // todo 需要加异常
            throw new RuntimeException("计算获取费用代号只允许由字母大小写、 \"_\"  + - * / ( )字符组成");
        }

        // 判断直接取值的conditionalExpr是否为单一字段，是就直接取值
        if (expr.matches("[a-zA-Z_]+")) {
            return directValue(adjustWrapper, type, expr);
        }

        Matcher matcher = compile.matcher(expr);
        // 准备容器保存
        HashMap<String, BigDecimal> fieldNameMap = new HashMap<>();
        // 对conditionalExpr表达式进行拆分获取fieldName
        while (matcher.find()) {
            String fieldName = matcher.group();
            // 获取fieldName具体值并保存到fieldNameMap
            fieldNameMap.put(fieldName, autoGetValue(adjustWrapper, type, fieldName));
        }

        // 使用calculateByJexl计算表达式
        return CalculateUtils.calculateByJexl(expr, fieldNameMap);
    }

    /**
     * 直接获取具体值
     *
     * @param adjustWrapper 待处理对象
     * @param type          封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param fieldName     需要获取的属性名字（下划线命名方式）
     * @return 获取到的值
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private BigDecimal directValue(SweAdjustWrapper adjustWrapper, String type, String fieldName) {
        // 判断直接取值的conditionalExpr是否为单一字段
        if (StringUtils.isBlank(fieldName) || !fieldName.matches("[a-zA-Z_]+")) {
            // todo 需要加异常
            throw new RuntimeException("直接取值费用代号只允许由字母大小写和 \"_\" 字符组成");
        }

        // 自动取值
        return autoGetValue(adjustWrapper, type, fieldName);
    }

    /**
     * 作用对象判断方法
     *
     * @param feeCodeWrapper
     * @param feeCodeConditional
     * @return
     */
    private List<SweAdjustWrapper> actionOn(SweFeeCodeWrapper feeCodeWrapper, FeeCodeConditional feeCodeConditional) {
        switch (feeCodeConditional.getActionOn()) {
            // todo 目前不支持获取父类条件
            case FeeCodeConditionalActionOnConstant.PARENT:
                return null;
            // 作用于自己，返回adjustWrapper自身
            case FeeCodeConditionalActionOnConstant.SELF:
                return Optional.ofNullable(feeCodeWrapper.getAdjustWrapper())
                        .map(Collections::singletonList)
                        .orElse(null);
            // 作用于子类，返回adjustWrapper的子类childList
            case FeeCodeConditionalActionOnConstant.CHILD:
                return Optional.ofNullable(feeCodeWrapper)
                        .map(SweFeeCodeWrapper::getAdjustWrapper)
                        .map(SweAdjustWrapper::getChildList)
                        .orElse(null);
            // 无法匹配，直接报错
            default:
                throw new RuntimeException("费用代号关系对象作用对象 FeeCodeConditional.actionOn=" + feeCodeConditional.getActionOn() + " 无法解析");
        }
    }

    /**
     * 自动判断子目还是还是最下层指标/清单，进行取值
     *
     * @param adjustWrapper 待处理对象
     * @param type          封装数据类型(0子目 1最下层指标/清单 2清单法)
     * @param fieldName     需要获取的属性名字（下划线命名方式）
     * @return
     */
    private static BigDecimal autoGetValue(SweAdjustWrapper adjustWrapper, String type, String fieldName) {

        Object resultObj = getValue(adjustWrapper, fieldName);

        if (!(resultObj instanceof BigDecimal)) {
            // todo 需要加异常
            throw new RuntimeException("费用代号处理器处暂不支持解析" + type + "类型数据");
        }

        BigDecimal result = (BigDecimal) resultObj;
        // 判断费用代号是子目还是最下层指标/清单
        switch (type) {
            // 如果是子目，直接取值
            case FeeCodeScopeConstant.ITEM:
                return result;
            // 如果是最下层指标/清单，需要*工程量
            case FeeCodeScopeConstant.INDEX:
                return result.multiply(
                        Optional.ofNullable(adjustWrapper)
                                .map(SweAdjustWrapper::getQuantity)
                                .orElseThrow(() -> new RuntimeException("费用代号处理器处理最下层指标/清单时，item对象工作量为空"))
                );
            default:
                // todo 需要加异常
                throw new RuntimeException("费用代号处理器处暂不支持解析" + type + "类型数据");
        }

    }

    /**
     * 直接通过反射获取feeCodeWrapper对象的属性
     *
     * @param adjustWrapper 待处理对象
     * @param fieldName     需要获取的属性名字（下划线命名方式）
     * @return
     */
    private static Object getValue(SweAdjustWrapper adjustWrapper, String fieldName) {
        // 转换命名方式为驼峰命名
        String fieldNameStr = underScoreToCamelCase(fieldName);

        // 通过反射获取adjustWrapper对象的属性
        try {
            Field declaredField = adjustWrapper.getClass().getDeclaredField(fieldNameStr);
            return declaredField.get(adjustWrapper);
        } catch (NoSuchFieldException e) {
            // todo 需要加异常
            throw new RuntimeException("FeeCodeEntity无法找到" + fieldNameStr + "字段属性");
        } catch (IllegalAccessException e) {
            // todo 需要加异常
            throw new RuntimeException("FeeCodeEntity无法找到" + fieldNameStr + "字段值");
        }
    }


    /**
     * 将下划线命名字符串转换为驼峰命名字符串
     *
     * @param underScore 下划线命名字符串
     * @return 驼峰命名字符串
     */
    private static String underScoreToCamelCase(String underScore) {
        // 判断非空 todo 需要完善判断，只允许传入下划线命名
        if (StringUtils.isBlank(underScore) || !underScore.matches("[a-zA-Z0-9_]+")) {
            // todo 需要加异常
            throw new RuntimeException("下划线命名转换驼峰命名异常，转换对象为：" + underScore);
        }

        // 进行转换
        StringBuffer result = new StringBuffer();
        boolean nextUpperCase = false;
        for (int i = 0; i < underScore.length(); i++) {
            char currentChar = underScore.charAt(i);

            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return result.toString();
    }
}
