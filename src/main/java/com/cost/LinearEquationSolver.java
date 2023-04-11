package com.cost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 反推demo
 * 不需要看
 */
public class LinearEquationSolver {

    public static List<BigDecimal[]> solve(BigDecimal num1, BigDecimal num2, BigDecimal num3, BigDecimal sum) {
        int i = 0;
        // 定义累加值
        BigDecimal decimal = new BigDecimal(1);
        // 通过方程式x*num1 + y*num2 + z*num3 = sum，计算方程式xyz
        System.out.println("目标结果为：" + sum);

        // 保存可能正确的公式
        List<BigDecimal[]> exprList = new ArrayList<>();

        // 遍历x可能取值
        for (BigDecimal x = new BigDecimal(0); x.compareTo(decimal) <= 0; x = x.add(decimal)) {
            // 遍历y可能取值
            for (BigDecimal y = new BigDecimal(0); y.compareTo(decimal) <= 0; y = y.add(decimal)) {
                // 遍历z可能取值
                for (BigDecimal z = new BigDecimal(0); z.compareTo(decimal) <= 0; z = z.add(decimal)) {
                    // 计算方程式
                    BigDecimal result = num1.multiply(x).add(num2.multiply(y)).add(num3.multiply(z));
                    System.out.println("第" + i + "次推算结果为：");
                    System.out.println("    公式为：" + x + "*num1 + " + y + "*num2 + " + z + "*num3 = " + result);
                    i++;
                    if (0 == result.compareTo(sum)) {
                        exprList.add(new BigDecimal[]{x, y, z});
                    }
                }
            }
        }
        if (null == exprList.get(0)) {
            System.out.println("没有计算出结果");
            return null;
        }
        return exprList;
    }

    public static void calculateCoefficients(BigDecimal D, BigDecimal... values) {
        // 获取参数个数
        int n = values.length;
        // 计算组合总数，等价于2^n
        int maxCombinations = 1 << n;

        // 循环遍历每种组合
        for (int i = 0; i < maxCombinations; i++) {
            // 初始化sum为0
            BigDecimal sum = BigDecimal.ZERO;

            // 遍历当前组合下的所有参数
            for (int j = 0; j < n; j++) {
                // 判断当前位是否为1
                if ((i & (1 << j)) != 0) {
                    // 如果是1，则将对应的值加到sum中
                    sum = sum.add(values[j]);
                }
            }

            // 如果sum等于D，则输出每个参数的系数
            if (sum.compareTo(D) == 0) {
                // 继续遍历当前组合下的所有参数
                for (int j = 0; j < n; j++) {
                    // 输出每个参数的系数，如果为1则输出1，否则输出0
                    System.out.print((i & (1 << j)) != 0 ? "1" : "0");
                }
                // 换行
                System.out.println();
            }
        }
    }
}