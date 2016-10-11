package uva;

import java.util.Scanner;

public class FloydWarshall {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Scanner sc2 = new Scanner(line);
            while (sc2.hasNext()) {

            }
        }
        sc.close();
    }

    public double f() {
        int[][][] m = new int[1][1][1];
        return 0;
    }

    /**
     * dp
     */
//    public void f() {
//        int n = 0; // sizeof graph
//        int[][] m = new int[n][n]; // adjacency matrix
//
//        for (int k = 0; k < n; k++) {
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < n; j++) {
//                    int tmp = m[i][k] + m[k][j];
//                    if (tmp <= m[i][j]) {
//                        m[i][j] = tmp;
//                    }
//                }
//            }
//        }
//    }

    /**
     * naive recursive
     */
//    public double f(int i, int j, int k) {
//        if (k == 0) {
//            return w(i, j);
//        }
//        double d1 = f(i, j, k - 1);
//        double d2 = f(i, k, k - 1) + f(k, j, k - 1);
//        return Math.min(d1, d2);
//    }
//
//    public double w(int i, int j) {
//        return 0;
//    }

//    public void pprint() {
//        for (int d = 0; d < n; d++) {
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < n; j++) {
//                    System.out.printf("%10.4f\t", m[d][i][j]);
//                }
//                System.out.println();
//            }
//            System.out.println();
//        }
//    }
}
