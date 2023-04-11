package com.cost;

import java.util.*;

public class FeeCalculator {
    public static double calculateFee(String feeExpr, Map<String, Double> variables) {
        // 将feeExpr转换为逆波兰表达式

        // 通过( ) + - * /运算符分割feeExpr
        String[] exprFeeCodeArr = feeExpr.split("(?<=[-+*/()])|(?=[-+*/()])");
        // 创建一个双端队列，作为栈来使用
        Deque<Double> stack = new ArrayDeque<>();
        // 遍历exprFeeCodeArr中的每个元素
        for (String exprFeeCode : exprFeeCodeArr) {
            if (exprFeeCode.matches("\\d+(\\.\\d+)?")) {
                // 如果元素是数字，将数字推入栈中
                stack.push(Double.parseDouble(exprFeeCode));
            } else if (exprFeeCode.matches("[a-zA-Z]\\w*")) {
                // 如果元素是exprFeeCode，根据变量名称从Map集合中获取变量的值
                Double value = variables.get(exprFeeCode);
                if (value == null) {
                    throw new IllegalArgumentException("Variable " + exprFeeCode + " not found");
                }
                stack.push(value);
                // 将变量值推入栈中
            } else {
                // 如果元素是运算符
                double operand2 = stack.pop();
                // 从栈中弹出两个操作数
                double operand1 = stack.pop();
                switch (exprFeeCode) {
                    // 根据运算符类型进行计算
                    case "+":
                        stack.push(operand1 + operand2);
                        break;
                    case "-":
                        stack.push(operand1 - operand2);
                        break;
                    case "*":
                        stack.push(operand1 * operand2);
                        break;
                    case "/":
                        stack.push(operand1 / operand2);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operator: " + exprFeeCode);
                }
            }
        }
        if (stack.size() != 1) {
            // 如果栈中还有其他元素，则表达式有误
            throw new IllegalArgumentException("Invalid fee expression: " + feeExpr);
        }
        return stack.pop();
        // 返回最终的计算结果
    }

    public static void main(String[] args) {
        Map<String, Double> variables = new HashMap<>();
        //创建一个Map集合，用于保存变量的名称和值
        variables.put("var1", 1.0);
        //将变量名和值添加到Map集合中
        variables.put("var2", 3.0);
        variables.put("var3", 4.0);
        variables.put("var4", 2.0);
        variables.put("var5", 5.0);
        String feeExpr = "(var1+var2)*var4/var3";
        //定义费用表达式
        double fee = calculateFee(feeExpr, variables);
        //计算费用表达式的结果
        System.out.println("Fee: " + fee);
        //输出费用的结果
    }
}