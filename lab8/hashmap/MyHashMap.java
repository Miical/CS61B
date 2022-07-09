package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private double loadFactor;
    private int size;

    /** Constructors */
    public MyHashMap() {
        loadFactor = 0.75;
        size = 0;
        buckets = createTable(16);
    }

    public MyHashMap(int initialSize) {
        loadFactor = 0.75;
        size = 0;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        loadFactor = maxLoad;
        size = 0;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node> table[] = new Collection[tableSize];
        for (int i = 0; i < table.length; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    @Override
    public boolean containsKey(K key) {
        int bucketsID = Math.floorMod(key.hashCode(), buckets.length);
        for (Node item : buckets[bucketsID]) {
            if (item.key.equals(key)) {
               return true;
            }
        }
        return false;
    }

    @Override
    public V remove(K key) {
        int bucketsID = Math.floorMod(key.hashCode(), buckets.length);
        for (Node item : buckets[bucketsID]) {
            if (item.key.equals(key)) {
                buckets[bucketsID].remove(item);
                size--;
                return item.value;
            }
        }
        return null;
    }

    private void put(Collection<Node> table[], Node item) {
        int bucketsID = Math.floorMod(item.key.hashCode(), table.length);
        buckets[bucketsID].add(item);
    }

    private void expand() {
        Collection<Node> oldTable[] = buckets;
        buckets = createTable(oldTable.length * 2);
        for (Collection<Node> bucket : oldTable) {
            for (Node item : bucket) {
                put(buckets, item);
            }
        }
    }

    @Override
    public void put(K key, V value) {
        if (size / buckets.length >= loadFactor) {
            expand();
        }
        if (containsKey(key)) {
            remove(key);
        }
        put(buckets, new Node(key, value));
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        buckets = createTable(buckets.length);
    }

    @Override
    public V get(K key) {
        int bucketsID = Math.floorMod(key.hashCode(), buckets.length);
        for (Node item : buckets[bucketsID]) {
            if (item.key.equals(key)) {
                return item.value;
            }
        }
        return null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> st = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node item : bucket) {
                st.add(item.key);
            }
        }
        return st;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }
}
