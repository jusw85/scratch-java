package uva;

import java.util.Arrays;

public class Palindrome {

    public static void main(String[] args) {
        String s;
//        s = sort("");
//        lp(s);
//        s = sort("A");
//        lp(s);
//        s = sort("AB");
//        lp(s);
//        s = sort("ABC");
//        lp(s);
//        s = sort("TESING");
//        lp(s);
    }

    /**
     * lexicographic ordered permutation
     * <p/>
     * 1. visit (print)
     * 2. find firstchar j
     * 3. find ceil l
     * 4. rev
     * <p/>
     * ABC
     * ACB
     * BAC
     * BCA
     * CAB
     * CBA
     * <p/>
     * 14679
     * 14697
     * 14769
     * 14796
     * 14967
     * ...
     * ...
     * 69471   - j=2(4), l=3(7)
     * 69714   - j=3(1), l=4(4)
     * 69741   - j=0(6), l=2(7)
     * 71469
     * ...
     * 97614
     * 97641
     *
     * @param s sorted string
     */

    public static void lp(String s) {
        if (s == null || s.length() <= 0)
            return;
        char[] cs = s.toUpperCase().toCharArray();

        while (true) {
            pr(cs);

            int j = cs.length - 2;
            while (j >= 0 &&
                    (cs[j] >= cs[j + 1])) {
                j--;
            }
            if (j < 0)
                return;
//          cs[j+1] .. cs[length-1] is descending
            assert (cs[j] < cs[j + 1]);

            int l = cs.length - 1;
            while (l > j &&
                    (cs[j] >= cs[l])) {
                l--;
            }
            assert (l != j);
//          cs[l] is the smallest number above cs[j], j+1 <= l < cs.length
            assert (cs[l] > cs[j]);

            swap(cs, l, j);

            rev(cs, j + 1, cs.length - 1);
        }
    }

    private static void rev(char[] cs, int min, int max) {
        while (min <= max) {
            swap(cs, min++, max--);
        }
    }

    private static void swap(char[] cs, int l, int j) {
        char t = cs[j];
        cs[j] = cs[l];
        cs[l] = t;
    }

    public static void pr(char[] cs) {
        System.out.println(new String(cs));
    }

    private static String sort(String s) {
        char[] cs = s.toCharArray();
        Arrays.sort(cs);
        return new String(cs);
    }
}
