package uva;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class P102 {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null && line.length() > 0) {
            StringTokenizer token = new StringTokenizer(line);
            int[] vals = new int[9];
            for (int i = 0; i < vals.length; i++) {
                vals[i] = Integer.parseInt(token.nextToken());
            }
            P102 p = new P102();
            p.eval(vals);
            p.pprint();
        }
    }

    String[] ck = { "BCG", "BGC", "CBG", "CGB", "GBC", "GCB" };
    int[][] cv = { { 0, 5, 7 }, { 0, 4, 8 }, { 2, 3, 7 }, { 2, 4, 6 },
            { 1, 3, 8 }, { 1, 5, 6 } };

    public void eval(int[] vals) {
        int sum = 0;
        for (int i = 0; i < vals.length; i++) {
            sum += vals[i];
        }
        for (int i = 0; i < 6; i++) {
            int cs = sum;
            for (int j = 0; j < 3; j++) {
                cs -= vals[cv[i][j]];
            }
            if (cs < minval) {
                minval = cs;
                minidx = i;
            }
        }
    }

    int minval = Integer.MAX_VALUE;
    int minidx = 0;

    public void pprint() {
        System.out.println(ck[minidx] + " " + minval);
    }
}
