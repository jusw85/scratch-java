package codility;

// For assessment of Codility as an interviewing platform for candidates
// https://codility.com/programmers/lessons/2-arrays/cyclic_rotation/
/*
A zero-indexed array A consisting of N integers is given. Rotation of the array means that each element is shifted right by one index, and the last element of the array is also moved to the first place.

For example, the rotation of array A = [3, 8, 9, 7, 6] is [6, 3, 8, 9, 7]. The goal is to rotate array A K times; that is, each element of A will be shifted to the right by K indexes.

Write a function:

    class Solution { public int[] solution(int[] A, int K); }

that, given a zero-indexed array A consisting of N integers and an integer K, returns the array A rotated K times.

For example, given array A = [3, 8, 9, 7, 6] and K = 3, the function should return [9, 7, 6, 3, 8].

Assume that:

        N and K are integers within the range [0..100];
        each element of array A is an integer within the range [−1,000..1,000].

In your solution, focus on correctness. The performance of your solution will not be the focus of the assessment.
 */
public class Codility2a {

    private static void rotateForwardOnce(int[] a) {
        int curr = a[0];
        for (int i = 0; i < a.length; i++) {
            int nextIdx = (i + 1) % a.length;
            int next = a[nextIdx];
            a[nextIdx] = curr;
            curr = next;
        }
    }

    /**
     * Time O(kn)
     * Space O(1)
     */
    public static int[] rotateArray(int[] a, int k) {
        if (a != null && a.length > 0) {
            k %= a.length;
            for (int j = 0; j < k; j++) {
                rotateForwardOnce(a);
            }
        }
        return a;
    }

    /**
     * Time O(n)
     * Space O(k)
     */
    public static int[] rotateArray2(int[] a, int k) {
        if (a != null && a.length > 0) {
            k %= a.length;
            int[] tmp = new int[k];
            for (int i = 0; i < k; i++) {
                tmp[i] = a[a.length - k + i];
            }
            for (int i = a.length - k - 1; i >= 0; i--) {
                a[i + k] = a[i];
            }
            for (int i = 0; i < k; i++) {
                a[i] = tmp[i];
            }
        }
        return a;
    }

    public static void main(String[] args) {
        int[] a = {3, 8, 9, 7, 6};
        a = rotateArray(a, 2);
        String prefix = "";
        for (int i = 0; i < a.length; i++) {
            System.out.print(prefix + a[i]);
            prefix = ", ";
        }
    }

}
