package deque;

import jh61b.junit.In;

import java.util.Iterator;
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int front, tail;
    private T[] items;

    /**
     * Create an empty deque.
     */
    public ArrayDeque() {
        tail = front = 0;
        items = (T []) new Object[8];
    }

    /**
     * Returns the number of items in the deque.
     */
    public int size() {
        return (tail - front + items.length) % items.length;
    }

    /**
     * Returns true if the item array is full.
     */
    private boolean isFull() {
        return (tail + 1) % items.length == front;
    }

    /**
     * Reset the array to newSize size.
     */
    private void resize(int newSize) {
        T[] newItem = (T []) new Object[newSize];
        for (int i = 0; i < size(); i++) {
            newItem[i] = get(i);
        }
        tail = size();
        front = 0;
        items = newItem;
    }

    /**
     * Adds an item of type T to the front of the deque.
     */
    public void addFirst(T item) {
        if (isFull()) {
            resize(items.length * 2);
        }
        front = (front - 1 + items.length) % items.length;
        items[front] = item;
    }

    /**
     * Adds an item of type T to the back of the deque.
     */
    public void addLast(T item) {
        if (isFull()) {
            resize(items.length * 2);
        }
        items[tail] = item;
        tail = (tail + 1) % items.length;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        for (int i = 0; i < size(); i++) {
            System.out.print(get(i));
            System.out.print(' ');
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = items[front];
        items[front] = null;
        front = (front + 1) % items.length;
        if (size() < items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T item = get(size() - 1);
        tail = (tail - 1 + items.length) % items.length;
        items[tail] = null;
        if (size() < items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    /**
     * Gets the item at the given index.
     * If no such item exists, returns null.
     */
    public T get(int index) {
        if (index < 0 || size() <= index) {
            return null;
        }
        return items[(front + index) % items.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        ArrayDequeIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            T returnItem = get(index);
            index += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) obj;
        if (this.size() != o.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!this.get(i).equals(o.get(i))) {
                return false;
            }
        }
        return true;
    }
}
