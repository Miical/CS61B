package deque;

import java.util.List;

public class LinkedListDeque<T> {
    private ListNode sentinel;
    private int size;

    public class ListNode {
        public ListNode prev;
        public T item;
        public ListNode next;
    }

    /**
     * Create an empty deque.
     */
    public LinkedListDeque() {
        sentinel = new ListNode();
        sentinel.prev = sentinel.next = sentinel;
        size = 0;
    }

    /**
     * Adds an item of type T to the front of the deque.
     */
    public void addFirst(T item) {
        ListNode newNode = new ListNode();
        newNode.prev = sentinel;
        newNode.item = item;
        newNode.next = sentinel.next;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    /**
     * Adds an item of type T to the back of the deque.
     */
    public void addLast(T item) {
       ListNode newNode = new ListNode()  ;
       newNode.prev = sentinel.prev;
       newNode.item = item;
       newNode.next = sentinel;
       sentinel.prev.next = newNode;
       sentinel.prev = newNode;
       size += 1;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the deque.
     */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        ListNode node = sentinel.next;
        while (node != sentinel) {
            System.out.print(node.item);
            System.out.print(' ');
            node = node.next;
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T item = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return item;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    public T removeLast() {
       if (size == 0) {
           return null;
       }
       T item = sentinel.prev.item;
       sentinel.prev.prev.next = sentinel.prev;
       sentinel.prev = sentinel.prev.prev;
       size -= 1;
       return item;
    }

    /**
     * Gets the item at the given index.
     * If no such item exists, returns null.
     */
    public T get(int index) {
        if (index < 0 || size <= index) {
            return null;
        }
        ListNode node = sentinel.next;
        for (int i = 1; i <= index; i++) {
            node = node.next;
        }
        return node.item;
    }

    /**
     * Gets the item at the given index.
     * If no such item exists, returns null.
     * Same as get, but uses recursion.
     */
    public T getRecursive(int index) {
        if (index < 0 || size <= index) {
            return null;
        }
        return getRecursive(sentinel.next, index);
    }

    private T getRecursive(ListNode node, int index) {
        if (index == 0) {
            return node.item;
        }
        return getRecursive(node.next, index - 1);
    }
}
