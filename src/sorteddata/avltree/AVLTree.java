package sorteddata.avltree;

import sorteddata.SortedData;

import java.util.*;

public class AVLTree<T> extends SortedData<T> {
    private static Random random;
    private final Comparator<T> comparator;
    private AVLNode<T> root;

    public AVLTree(Comparator<T> comparator) {
        this(comparator, new AVLNodeEmpty<T>(comparator));
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private AVLTree(Comparator<T> comparator, AVLNode<T> root) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public AVLTree<T> clone() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean insert(T element) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T get(T value) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T getRandom() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T getAtIndex(int i) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public Iterator<T> getRange(T start, int count, boolean backwards) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

