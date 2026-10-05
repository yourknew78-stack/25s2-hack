package sorteddata.bstree;

import sorteddata.SortedData;

import java.util.*;

public class BSTree<T> extends SortedData<T> {
    private static Random random;
    private final Comparator<T> comparator;
    private BSNode<T> root;

    public BSTree(Comparator<T> comparator) {
        this(comparator, new BSNodeEmpty<T>(comparator));
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private BSTree(Comparator<T> comparator, BSNode<T> root) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public BSTree<T> clone() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean insert(T element) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T get(T value) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T getAtIndex(int i) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T getRandom() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public Iterator<T> getRange(T start, int count, boolean backwards) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

