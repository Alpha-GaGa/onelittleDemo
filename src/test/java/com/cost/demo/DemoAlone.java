package com.cost.demo;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoAlone {

    /**
     * feeExpr中费用代号Set
     */
    private final Set<String> exprFeeCodeSet = new HashSet<>();


    /**
     * 筛选器
     */
    public static final Pattern compile = Pattern.compile("[a-zA-Z0-9_]+");


    @Test
    public void test(){

        String feeExpr = "114514.4";
        // 如果feeExpr非空，获取公式中包含的费用代号
        if (StringUtils.isNotBlank(feeExpr)) {
            // 如果是不是纯数字
            if( feeExpr.matches("[a-zA-Z_]+")){
                // todo 是否除了数字、英文、_ 以为的费用代号组成
                Matcher matcher = compile.matcher(feeExpr);
                while (matcher.find()) {
                    System.out.println("进该方法一次");
                    // 把公式中包含的费用代号保存到exprFeeCodeSet
                    exprFeeCodeSet.add(matcher.group());
                }
                return;
            }

            // 如果是纯数字
            System.out.println("进行纯数字判断");

        }

    }

    @Test
    public void testConcat(){
        String feeExpr = "绝对独立费";
        System.out.println("\"独立费\".contains(feeExpr) = " + feeExpr.contains("独立费"));

    }
}
