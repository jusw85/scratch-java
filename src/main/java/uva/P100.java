package uva;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

class P100 {

    static String readln(int maxLg) { // utility function to read from stdin
        byte lin[] = new byte[maxLg];
        int lg = 0, car = -1;

        try {
            while (lg < maxLg) {
                car = System.in.read();
                if ((car < 0) || (car == '\n'))
                    break;
                lin[lg++] += car;
            }
        } catch (IOException e) {
            return (null);
        }

        if ((car < 0) && (lg == 0))
            return (null); // eof
        return (new String(lin, 0, lg));
    }

    public static void main(String[] args) {
        String input;
        StringTokenizer idata;
        int a, b, min, max;

        while ((input = P100.readln(255)) != null) {
            idata = new StringTokenizer(input);
            a = Integer.parseInt(idata.nextToken());
            b = Integer.parseInt(idata.nextToken());
            if (a < b) {
                min = a;
                max = b;
            } else {
                min = b;
                max = a;
            }
            P100 p = new P100();
            try {
                int r = p.range(min, max);
                System.out.println(a + " " + b + " " + r);
            } catch (Exception e) {
                System.out.println("err");
            }
        }
    }

    Map<Integer, Integer> matrix = new HashMap<Integer, Integer>();

    P100() {
        matrix.put(1, 1);
    }

    int range(int i, int j) {
        int max = -1;
        for (; i <= j; i++) {
            int c = c(i);
            if (c > max) {
                max = c;
            }
        }
        return max;
    }

    int c(int n) {
        int orig = n;

        List<Integer> path = new ArrayList<Integer>();
        while (matrix.get(n) == null) {
            path.add(n);
            if (n % 2 == 0) {
                n /= 2;
            } else {
                n = 3 * n + 1;
            }
        }
        for (int i = 0; i < path.size(); i++) {
            int idx = path.get(i);
            int count = matrix.get(n) + path.size() - i;
            matrix.put(idx, count);
        }
        return matrix.get(orig);
    }

}
