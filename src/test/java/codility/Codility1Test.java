package codility;

import org.junit.Assert;
import org.junit.Test;

public class Codility1Test {

    @Test
    public void testBinaryGap() throws Exception {
        Assert.assertEquals(Codility1.binaryGap(1), 0);
        Assert.assertEquals(Codility1.binaryGap(9), 2);
        Assert.assertEquals(Codility1.binaryGap(1041), 5);

        Assert.assertEquals(Codility1.binaryGap(3), 0);
        Assert.assertEquals(Codility1.binaryGap(4), 0);
    }

}
