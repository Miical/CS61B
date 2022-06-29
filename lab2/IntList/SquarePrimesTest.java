package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);

        lst = IntList.of(20, 21, 22);
        changed = IntListExercises.squarePrimes(lst);
        assertEquals("20 -> 21 -> 22", lst.toString());
        assertFalse(changed);


        lst = IntList.of(4, 5, 6, 7, 19);
        changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 25 -> 6 -> 49 -> 361", lst.toString());
        assertTrue(changed);
    }

}
