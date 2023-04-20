package com.cost.handler;

import com.cost.constant.FeeCodeConditionalActionOnConstant;
import com.cost.constant.FeeCodeConditionalTypeConstant;
import com.cost.constant.FeeCodeScopeConstant;
import com.cost.domain.SysFeeCodeDTO;
import com.cost.domain.common.FeeCodeConditional;
import com.cost.domain.wrapper.SweFeeCodeWrapper;
import com.cost.util.CalculateUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description 基础费用代号分析处理器接口
 * @Created zhangtianhao
 * @date 2023-04-20 18:35
 * @version
 */
public abstract class BasicsFeeCodeMatchHandler implements FeeCodeMatchHandler{

    /**
     * 筛选器
     */
    public static final Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");


    /**
     * 节点的费用代号分析
     *
     * @param item          需要解析的对象
     * @param sysFeeCodeDTO
     * @return 返回分析出来的结果
     */
    @Override
    public BigDecimal analysis(SweFeeCodeWrapper item, SysFeeCodeDTO sysFeeCodeDTO) {
        // 获取规则
        List<FeeCodeConditional> conditional = sysFeeCodeDTO.getConditional();
        BigDecimal result = BigDecimal.ZERO;
        // 按照规则计算
        conditional.stream().sorted(Comparator.comparing(FeeCodeConditional::getOrder)).forEach(feeCodeConditional -> {
            switch (feeCodeConditional.getType()) {
                // 直接取值
                case FeeCodeConditionalTypeConstant.DIRECT_VALUE:
                    result.add(directValue(item, feeCodeConditional));
                    break;
                // 计算取值
                case FeeCodeConditionalTypeConstant.CALCULATE:
                    result.add(calculate(item, feeCodeConditional));
                    break;
                // todo 另外三种配置需要添加
                // 无法匹配，直接报错
                default:
                    throw new RuntimeException("费用代号关系对象类型无法解析");
            }

        });
        return result;
    }

    /**
     * 通过计算获取值
     *
     * @param item               待处理对象
     * @param feeCodeConditional 费用代号条件对象
     * @return
     */
    private BigDecimal calculate(SweFeeCodeWrapper item, FeeCodeConditional feeCodeConditional) {
        // 判断是否只作用于自身
        if (!FeeCodeConditionalActionOnConstant.SELF.equals(feeCodeConditional.getActionOn())) {
            // todo 需要加异常
            throw new RuntimeException("计算获取费用代号只允许取自身的值，actionOn=1");
        }

        // 判断计算获取值的conditionalExpr是否为单一字段
        if (StringUtils.isBlank(feeCodeConditional.getConditionalExpr()) || !feeCodeConditional.getConditionalExpr().matches("[a-zA-Z_+\\-*/()]+")) {
            // todo 需要加异常
            throw new RuntimeException("计算获取费用代号只允许由字母大小写、 \"_\"  + - * / ( )字符组成");
        }

        // 判断直接取值的conditionalExpr是否为单一字段，是就直接取值
        if (feeCodeConditional.getConditionalExpr().matches("[a-zA-Z_]+")) {
            return directValue(item, feeCodeConditional);
        }

        Matcher matcher = compile.matcher(feeCodeConditional.getConditionalExpr());
        // 准备容器保存
        HashMap<String, BigDecimal> fieldNameMap = new HashMap<>();
        // 对conditionalExpr表达式进行拆分获取fieldName
        while (matcher.find()) {
            String fieldName = matcher.group();
            // 获取fieldName具体值并保存到fieldNameMap
            fieldNameMap.put(fieldName, autoGetValue(item, fieldName));
        }

        // 使用calculateByJexl计算表达式
        return CalculateUtils.calculateByJexl(feeCodeConditional.getConditionalExpr(), fieldNameMap);
    }

    /**
     * 直接获取具体值
     *
     * @param item               待处理对象
     * @param feeCodeConditional 费用代号条件对象
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private BigDecimal directValue(SweFeeCodeWrapper item, FeeCodeConditional feeCodeConditional) {
        // 判断是否只作用于自身
        if (!FeeCodeConditionalActionOnConstant.SELF.equals(feeCodeConditional.getActionOn())) {
            // todo 需要加异常
            throw new RuntimeException("直接取值费用代号只允许取自身的值，actionOn=1");
        }

        // 判断直接取值的conditionalExpr是否为单一字段
        if (StringUtils.isBlank(feeCodeConditional.getConditionalExpr()) || !feeCodeConditional.getConditionalExpr().matches("[a-zA-Z_]+")) {
            // todo 需要加异常
            throw new RuntimeException("直接取值费用代号只允许由字母大小写和 \"_\" 字符组成");
        }

        return autoGetValue(item, feeCodeConditional.getConditionalExpr());
    }

    /**
     * 自动判断子目还是还是最下层指标/清单，进行取值
     *
     * @param item      待处理对象
     * @param fieldName 需要获取的属性名字（下划线命名方式）
     * @return
     */
    private static BigDecimal autoGetValue(SweFeeCodeWrapper item, String fieldName) {
        // 判断费用代号是子目还是最下层指标/清单
        switch (item.getType()) {
            // 如果是子目，直接取值
            case FeeCodeScopeConstant.ITEM:
                return getValue(item, fieldName);
            // 如果是最下层指标/清单，需要*工程量
            case FeeCodeScopeConstant.INDEX:
                return getValue(item, fieldName).multiply(
                        Optional.ofNullable(item)
                                .map(SweFeeCodeWrapper::getQuantity)
                                .orElseThrow(() -> new RuntimeException("费用代号处理器处理最下层指标/清单时，item对象工作量为空"))
                );
            default:
                // todo 需要加异常
                throw new RuntimeException("费用代号处理器处暂不支持解析" + item.getType() + "类型数据");
        }

    }

    /**
     * 直接通过反射获取item对象的属性
     *
     * @param item      待处理对象
     * @param fieldName 需要获取的属性名字（下划线命名方式）
     * @return
     */
    private static BigDecimal getValue(SweFeeCodeWrapper item, String fieldName) {
        // 转换命名方式为驼峰命名
        String fieldNameStr = underScoreToCamelCase(fieldName);

        // 通过反射获取item对象的属性
        try {
            Field declaredField = SweFeeCodeWrapper.class.getDeclaredField(fieldNameStr);
            return (BigDecimal) declaredField.get(item);
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
