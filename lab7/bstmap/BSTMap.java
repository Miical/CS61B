package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    enum Color {
        BLACK, RED;
    }
    private class BSTNode {
        K key;
        V value;
        Color topEdgeColor;
        BSTNode left, right;

        public BSTNode(K k, V v, Color c) {
           key = k;
           value = v;
           topEdgeColor = c;
           left = right = null;
        }
    }

    private BSTNode root;
    private int size;

    public BSTMap() {
        size = 0;
        root = null;
    }

    private Color flipColor(Color c) {
        if (c == Color.RED) {
            return Color.BLACK;
        }
        return Color.RED;
    }
    /**
     * Flip the color of edges connected to node.
     */
    private void flipColor(BSTNode node) {
        if (node == null) {
            return;
        }

        node.topEdgeColor = flipColor(node.topEdgeColor);
        if (node.left != null) {
            node.left.topEdgeColor = flipColor(node.left.topEdgeColor);
        }
        if (node.right != null) {
            node.right.topEdgeColor = flipColor(node.right.topEdgeColor);
        }
    }

    /**
     * Rotate node to left.
     * @Return new root of subtree.
     */
    private BSTNode rotateLeft(BSTNode node) {
        BSTNode rightChild = node.right;
        if (rightChild == null) {
            throw new RuntimeException("Can't rotate a node without right child.");
        }

        // Move rightChild's left child to node's right child.
        node.right = null;
        if (rightChild.left != null) {
            node.right = rightChild.left;
        }

        // Move node to rightChild's left child.
        rightChild.left = node;

        return rightChild;
    }


    /**
     * Rotate node to right.
     * @Return new root of subtree.
     */
    private BSTNode rotateRight(BSTNode node) {
        BSTNode leftChild = node.left;
        if (leftChild == null) {
            throw new RuntimeException("Can't rotate a node without left child.");
        }

        // Move leftChild's right child to node's left child.
        node.left = null;
        if (leftChild.right != null) {
            node.left = leftChild.right;
        }

        // Move node to leftChild's right child.
        leftChild.right = node;

        return leftChild;
    }

    /**
     * Return true if the top edge of node is red.
     */
    private boolean isRed(BSTNode node) {
        if (node == null) {
            return false;
        } else {
            return node.topEdgeColor == Color.RED;
        }
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value, Color.RED);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) { node.left = put(node.left, key, value); }
        else if (cmp > 0) { node.right = put(node.right, key, value); }
        else { node.value = value; }

        if (isRed(node.right) && !isRed(node.left)) { node = rotateLeft(node); }
        if (isRed(node.left) && isRed(node.left.left)) { node = rotateRight(node); }
        if (isRed(node.left) && isRed(node.right)) { flipColor(node); }

        return node;
    }

    @Override
    public void put(K key, V value) {
        size += 1;
        root = put(root, key, value);
    }


    private V get(BSTNode node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) { return get(node.left, key); }
        else if (cmp > 0) { return get(node.right, key); }
        else { return node.value; }
    }
    @Override
    public V get(K key) {
        return get(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) {
            return false;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKey(node.left, key);
        } else if (cmp > 0) {
            return containsKey(node.right, key);
        } else {
            return true;
        }
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    @Override
    public int size() {
        return size;
    }

    private void clear(BSTNode node) {
        if (node == null) {
            return;
        }

        clear(node.left);
        clear(node.right);

        node.left = node.right = null;
    }
    @Override
    public void clear() {
        clear(root);
        root = null;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
