package codility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Codility2bTest {

    @Test
    public void testOddOccurrence() throws Exception {
        int[] in1 = {3, 8, 8, 3, 6};
        assertEquals(Codility2b.oddOccurrence(in1), 6);

        int[] in2 = {1};
        assertEquals(Codility2b.oddOccurrence(in2), 1);

        int[] in3 = {1, 1, 1};
        assertEquals(Codility2b.oddOccurrence(in3), 1);

        int[] in4 = {1, 2, 3, 1, 2};
        assertEquals(Codility2b.oddOccurrence(in4), 3);
    }

}
