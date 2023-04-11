package com.cost.util;

import com.cost.domain.common.TreeNode;

import java.math.BigDecimal;
import java.util.*;

public class MyCalculate {
    //private static <T extends TreeNode> void calculate(T node, Map<Long, List<T>> groupByParentId){
    //// 将feeExpr转换为逆波兰表达式进行计算
    //String feeExpr = CostAnalysePrice.getFeeExpr();
    //// 通过( ) + - * /运算符分割feeExpr
    //String[] exprFeeCodeArr = feeExpr.split("(?<=[-+*/()])|(?=[-+*/()])");
    //// 创建一个双端队列，作为栈来使用
    //Deque<BigDecimal> stack = new ArrayDeque<>();
    //    // 遍历exprFeeCodeArr中的每个元素
    //    for (String exprFeeCode : exprFeeCodeArr) {
    //        if (exprFeeCode.matches("[a-zA-Z0-9_]+")) {
    //            // 如果元素是exprFeeCode，从feeCodeMapping集合中获取对应的值
    //            BigDecimal bigDecimal = feeCodeMapping.get(exprFeeCode);
    //
    //            if (null == bigDecimal) {
    //                // todo 如果找不到对应的值，需要做什么处理
    //                throw new IllegalArgumentException("Variable " + exprFeeCode + " not found");
    //            }
    //
    //            // 将exprFeeCode的值推入栈中
    //            stack.push(bigDecimal);
    //        } else {
    //            // 如果是左括号，则递归计算括号内的表达式，并将计算结果作为一个操作数添加到操作数栈中
    //            if ("(".equals(exprFeeCode)) {
    //                int closingParen = findClosingParen(exprFeeCodeArr);
    //                String[] subtokens = Arrays.copyOfRange(exprFeeCodeArr, 1, closingParen);
    //                operands.push(calculate(subtokens, variables));
    //
    //                // 如果是右括号，则停止计算括号内的表达式，并将括号内表达式的计算结果作为一个操作数添加到操作数栈中
    //            } else if (")".equals(exprFeeCode)) {
    //                break;
    //            } else { // 如果是运算符，则将其添加到运算符栈中，并继续处理下一个操作数
    //                while (!operators.isEmpty() && precedence(operator) <= precedence(operators.peek())) {
    //                    applyOperation(operands, operators.pop());
    //                }
    //                operators.push(operator);
    //            }
    //
    //            // 如果元素是运算符，从栈中弹出两个操作数
    //            BigDecimal operand2 = stack.pop();
    //            BigDecimal operand1 = stack.pop();
    //
    //            // 根据运算符类型进行计算
    //            switch (exprFeeCode) {
    //                case "+":
    //                    stack.push(operand1.add(operand2));
    //                    break;
    //                case "-":
    //                    stack.push(operand1.subtract(operand2));
    //                    break;
    //                case "*":
    //                    stack.push(operand1.multiply(operand2));
    //                    break;
    //                case "/":
    //                    stack.push(operand1.divide(operand2));
    //                    break;
    //                default:
    //                    // todo 如果不是指定的标点符号，怎么处理
    //                    throw new IllegalArgumentException("Invalid operator: " + exprFeeCode);
    //            }
    //        }
    //    }
    //
    //    if (stack.size() != 1) {
    //        // todo 如果栈中还有其他元素，则表达式有误，怎么处理
    //        throw new IllegalArgumentException("Invalid fee expression: " + feeExpr);
    //    }
    //}

    /**
     *
     * @param tokens
     * @return
     */
    private static int findClosingParen(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("(")) {
                stack.push(i);
            } else if (tokens[i].equals(")")) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("No matching opening parenthesis found");
                }
                stack.pop();
                if (stack.isEmpty()) {
                    return i;
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("No matching closing parenthesis found");
        }
        throw new IllegalArgumentException("No closing parenthesis found");
    }
}
