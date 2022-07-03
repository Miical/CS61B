package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> cmp;

    /**
     * Creates a MaxArrayDeque with the given Comparator.
     */
    public MaxArrayDeque(Comparator<T> c) {
        cmp = c;
    }

    /**
     * Returns the maximum element in the deque as governed
     * by the given Comparator c.
     * If the MaxArrayDeque is empty, return null.
     */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            T item = get(i);
            if (c.compare(maxItem, item) < 0) {
               maxItem = item;
            }
        }
        return maxItem;
    }

    /**
     * Returns the maximum element in the deque as governed
     * by the default Comparator;
     * If the MaxArrayDeque is empty, return null.
     */
    public T max() {
        return max(cmp);
    }
}
