package sorteddata.bstree;

import java.util.Comparator;

class BSNodeFilled<T> extends BSNode<T> {
    final BSNode<T> left, right;
    final T value;
    private final int height, size;
    public BSNodeFilled(Comparator<T> comparator, T value, BSNode<T> left, BSNode<T> right) {
        super(comparator);
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public int height() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public String toString() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public BSNodeFilled<T> insert(T element) {
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

