package sorteddata.avltree;

import java.util.Comparator;

class AVLNodeFilled<T> extends AVLNode<T> {
    final AVLNode<T> left, right;
    final T value;
    private final int height, balance, size;
    public AVLNodeFilled(Comparator<T> comparator, T value, AVLNode<T> left, AVLNode<T> right) {
        super(comparator);
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public int height() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
    public int balanceFactor() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
    public int size() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public AVLNodeFilled<T> insert(T element) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T getAtIndex(int i) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public boolean contains(T element) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public T get(T element) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

