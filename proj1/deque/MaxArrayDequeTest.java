package deque;

import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
   @Test
   public void testInteger() {
       MaxArrayDeque<Integer> arr = new MaxArrayDeque<>(new Comparator<Integer>() {
           @Override
           public int compare(Integer o1, Integer o2) {
               return o1 - o2;
           }
       });
       for (int i = 1; i <= 10; i++) {
           arr.addFirst(i);
       }

       int expected = 10;
       int actual = arr.max();
       assertEquals(expected, actual);

       expected = 1;
       actual = arr.max(new Comparator<Integer>() {
           @Override
           public int compare(Integer o1, Integer o2) {
               return o2- o1;
           }
       });
       assertEquals(expected, actual);
   }
}
