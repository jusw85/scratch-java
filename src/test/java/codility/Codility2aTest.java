package codility;

import org.junit.Assert;
import org.junit.Test;

public class Codility2aTest {

    @Test
    public void testRotateArray() throws Exception {
        testRotateGeneric(Codility2a::rotateArray);
    }

    @Test
    public void testRotateArray2() throws Exception {
        testRotateGeneric(Codility2a::rotateArray2);
    }

    public void testRotateGeneric(Rotator rotator) throws Exception {
        int[] in1 = {3, 8, 9, 7, 6};
        int[] result1 = rotator.rotateArray(in1, 1);
        int[] expected1 = {6, 3, 8, 9, 7};
        Assert.assertArrayEquals(expected1, result1);

        int[] in2 = {};
        int[] result2 = rotator.rotateArray(in2, 1);
        int[] expected2 = {};
        Assert.assertArrayEquals(expected2, result2);

        int[] in3 = null;
        int[] result3 = rotator.rotateArray(in3, 1);
        int[] expected3 = null;
        Assert.assertArrayEquals(expected3, result3);

        int[] in4 = {3, 8, 9, 7, 6};
        int[] result4 = rotator.rotateArray(in4, 2);
        int[] expected4 = {7, 6, 3, 8, 9};
        Assert.assertArrayEquals(expected4, result4);

        int[] in5 = {1, 2, 3};
        int[] result5 = rotator.rotateArray(in5, 7);
        int[] expected5 = {3, 1, 2};
        Assert.assertArrayEquals(expected5, result5);

        int[] in6 = {1, 2, 3};
        int[] result6 = rotator.rotateArray(in6, 3);
        int[] expected6 = {1, 2, 3};
        Assert.assertArrayEquals(expected6, result6);
    }

    private interface Rotator {
        int[] rotateArray(int[] a, int k);
    }

}
