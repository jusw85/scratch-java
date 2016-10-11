package codility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Codility3aTest {

    @Test
    public void testFrogJump() throws Exception {
        assertEquals(Codility3a.frogJump(10, 85, 30), 3);
        assertEquals(Codility3a.frogJump(1, 2, 1), 1);
        assertEquals(Codility3a.frogJump(1, 2, 10), 1);
    }
}
