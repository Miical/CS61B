package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
       AListNoResizing<Integer> arr = new AListNoResizing<>();
       BuggyAList<Integer> bugArr = new BuggyAList<>();
       for (int i = 4; i <= 6; i++) {
           arr.addLast(i);
           bugArr.addLast(i);
       }
       for (int i = 1; i <= 3; i++) {
           assertEquals(arr.removeLast(), bugArr.removeLast());
       }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = correct.size();
                int size2 = broken.size();
                assertEquals(size, size2);
            } else if (operationNumber == 2) {
                // removeLast
                if (correct.size() > 0) {
                    int val = correct.removeLast();
                    int val2 = broken.removeLast();
                    assertEquals(val, val2);
                }
            }
        }
    }
}
