package uva;

import org.apache.commons.math3.util.Precision;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Fill in operators in the blanks between numbers to achieve output.
 * <p>
 * TODO: Memoize intermediate results
 */
public class OperatorSearch {

//  public static char[] ops = { '+', '*', '-', '/', '*' };
//  public static double[] in = { 2, 5, 3, 14, 7, 10 };

//    public static double[] in = { 2, 5, 3, 8, 7, 10 };
//    public static double out = 14;

    public static double[] in = {1, 2, 13, 5, 8, 2, 7, 25, 8, 15, 9, 3, 6, 7,
            3, 6, 32, 46};
    public static double out = 999;

    public static char[] ops = new char[in.length - 1];

    public static void main(String args[]) {
        for (int i = 0; i < ops.length; i++) {
            ops[i] = 0;
        }
        dfs(0);
    }

    public static void dfs(int idx) {
        if (idx == ops.length) {
            if (Precision.equals(evaluate(), out, 0.01)) {
                pprint();
            }
            return;
        }
        ops[idx] = '+';
        dfs(idx + 1);
        ops[idx] = '-';
        dfs(idx + 1);
        ops[idx] = '*';
        dfs(idx + 1);
        ops[idx] = '/';
        dfs(idx + 1);
    }

    private static void pprint() {
        for (int i = 0; i < in.length - 1; i++) {
            System.out.print(in[i] + " ");
            System.out.print(ops[i] + " ");
        }
        System.out.println(in[in.length - 1]);
    }

    public static double evaluate() {
        Stack<Double> stack = new Stack<>();
        Queue<Character> opstack = new LinkedList<>();
        stack.push(in[0]);

        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case '+':
                case '-':
                    opstack.offer(ops[i]);
                    stack.push(in[i + 1]);
                    break;
                case '*':
                    stack.push(stack.pop() * in[i + 1]);
                    break;
                case '/':
                    stack.push(stack.pop() / in[i + 1]);
                    break;
            }
        }

        double result = stack.get(0);
        for (int i = 1; i < stack.size(); i++) {
            switch (opstack.poll()) {
                case '+':
                    result += stack.get(i);
                    break;
                case '-':
                    result -= stack.get(i);
                    break;
            }
        }
        return result;
    }

}
