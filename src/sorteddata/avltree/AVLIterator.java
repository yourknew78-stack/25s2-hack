package sorteddata.avltree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

public class AVLIterator<T> implements Iterator<T> {
    private final Stack<AVLNodeFilled<T>> search;
    private final boolean backwards;

    private int count;

    AVLIterator(T begin, AVLNode<T> root, Comparator<T> comparator, int count, boolean backwards) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public T next() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

