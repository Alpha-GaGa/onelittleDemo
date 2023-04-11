package com.cost.demo;

import com.cost.LinearEquationSolver;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 反推Demo
 */
public class CalculateDemo {
    public static void main(String[] args) {
        // 人工
        BigDecimal A = new BigDecimal("1229.58");
        // 材料
        BigDecimal B = new BigDecimal("4091.77");
        // 机械
        BigDecimal C = new BigDecimal("242.46");
        // 管理费
        BigDecimal D = new BigDecimal("439.2877284").divide(new BigDecimal("19.54").divide(new BigDecimal("100")));
        List<BigDecimal[]> DExpr = LinearEquationSolver.solve(A, B, C, D);
        DExpr.forEach(arrays -> System.out.println("管理费公式可能为：" + Arrays.toString(arrays)));

        BigDecimal E = new BigDecimal("291.437561").divide(new BigDecimal("0.07"));
        LinearEquationSolver.calculateCoefficients(E,A,B,C,D);

        BigDecimal F = new BigDecimal("4163.393728").divide(new BigDecimal("1"));
        LinearEquationSolver.calculateCoefficients(F,A,B,C,D,E);

    }

    //public class BinaryIntegerProgramming {
    //    private int n;
    //    private int[] a1, a2;
    //    private int[] b1, b2;
    //    private int[] c1, c2;
    //    // ...
    //    private int z;
    //
    //    public BinaryIntegerProgramming(/* pass in parameters */) {
    //        // initialize instance variables
    //    }
    //
    //    public int[] solve() {
    //        int[] x = new int[n];
    //        int[] y = new int[n];
    //        int[] A = new int[n];
    //        int[] B = new int[n];
    //        int[] C = new int[n];
    //        // ...
    //        boolean[] used = new boolean[n];
    //        Arrays.fill(used, false);
    //        // initialize lower and upper bounds for each variable
    //        int[] lb = new int[n];
    //        int[] ub = new int[n];
    //        Arrays.fill(lb, 0);
    //        Arrays.fill(ub, 1);
    //        // initialize the best solution found so far
    //        int[] bestSol = new int[n];
    //        Arrays.fill(bestSol, -1);
    //        int bestObj = Integer.MAX_VALUE; // set to a very large value initially
    //        // start the branch-and-bound search
    //        backtrack(0, x, y, A, B, C, /* ... */, used, lb, ub, bestSol, bestObj);
    //        return bestSol;
    //    }
    //
    //    private void backtrack(int k, int[] x, int[] y, int[] A, int[] B, int[] C,
    //            /* ... */, boolean[] used, int[] lb, int[] ub, int[] bestSol, int bestObj) {
    //        if (k == n) { // base case: all variables have been assigned values
    //            int obj = evaluate(x, y, A, B, C, /* ... */); // evaluate the objective function
    //            if (obj < bestObj) { // update the best solution found so far
    //                System.arraycopy(x, 0, bestSol, 0, n);
    //                bestObj = obj;
    //            }
    //        } else { // recursive case: assign a value to variable k
    //            for (int v = lb[k]; v <= ub[k]; v++) {
    //                if (!used[k]) { // only try unassigned variables
    //                    if (isFeasible(x, y, A, B, C, /* ... */)) { // check feasibility
    //                        used[k] = true;
    //                        if (v == 0) {
    //                            // assign 0 to variable k
    //                            // update x, y, A, B, C, etc. accordingly
    //                        } else {
    //                            // assign 1 to variable k
    //                            // update x, y, A, B, C, etc. accordingly
    //                        }
    //                        backtrack(k + 1, x, y, A, B, C, /* ... */, used, lb, ub, bestSol, bestObj);
    //                        used[k] = false;
    //                        // undo the changes made to x, y, A, B, C, etc.
    //                    }
    //                }
    //            }
    //        }
    //    }
    //
    //    private boolean isFeasible(int[] x, int[] y, int[] A, int[] B, int[] C,
    //            /* ... */) {
    //        // check if the current assignment of variables satisfies all constraints
    //        int lhs = 0;
    //        for (int i = 0; i < n; i++) {
    //            lhs += A[i] * (x[i] * a1[i] + y[i] * a2[i])
    //                    + B[i] * (x[i] * b1[i] + y[i] * b2[i])
    //                    + C[i] * (x[i] * c1[i] + y[i] * c2[i]);
    //            // ...
    //        }
    //        return lhs <= z;
    //    }
    //
    //    private int evaluate(int[] x, int[] y, int[] A, int[] B, int[] C,
    //            /* ... */) {
    //        // compute the value of the objective function for the current solution
    //        int rhs = 0;
    //        for (int i = 0; i < n; i++) {
    //            rhs += A[i] * (x[i] * a1[i] + y[i] * a2[i])
    //                    + B[i] * (x[i] * b1[i] + y[i] * b2[i])
    //                    + C[i] * (x[i] * c1[i] + y[i] * c2[i]);
    //            // ...
    //        }
    //        return rhs;
    //    }
    //}
}
