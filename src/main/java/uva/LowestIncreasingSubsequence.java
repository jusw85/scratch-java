package uva;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LowestIncreasingSubsequence {

    public static void main(String[] args) throws Exception {
        int[] x = { 1, 2, 1000, 3, -1 };
        List<Integer> list = lis(x, x.length - 1);
        System.out.println(list);
    }

//    static Map<Integer, List<Integer>> mem = new HashMap<>();

    private static List<Integer> lis(int[] x, int n) {
        if (n == 0) {
            List<Integer> list = new ArrayList<Integer>();
            list.add(x[0]);
            return list;
        }
//        if (mem.containsKey(n)) {
//            return mem.get(n);
//        }
        List<Integer> maxlist = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            List<Integer> list = lis(x, i);
            if (list.size() >= maxlist.size()) {
                maxlist = list;
            }
        }
        int l1 = maxlist.get(maxlist.size() - 1);
        int l2 = x[n];
        if (l2 > l1) {
            maxlist.add(l2);
        }
        return maxlist;
    }

}
