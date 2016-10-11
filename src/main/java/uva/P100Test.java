package uva;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class P100Test {

    @Test
    public void single() {
        P100 p = new P100();
        int c;

        c = p.c(1);
        assertEquals(1, c);

        c = p.c(2);
        assertEquals(2, c);

        c = p.c(4);
        assertEquals(3, c);

        c = p.c(22);
        assertEquals(16, c);
    }

    @Test
    public void range() {
        P100 p = new P100();
        int max;

        max = p.range(1, 10);
        assertEquals(20, max);

        max = p.range(100, 200);
        assertEquals(125, max);

        max = p.range(201, 210);
        assertEquals(89, max);

        max = p.range(900, 1000);
        assertEquals(174, max);

        max = p.range(1, 110000);
        assertEquals(354, max);
    }
}
