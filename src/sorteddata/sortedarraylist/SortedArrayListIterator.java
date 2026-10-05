package sorteddata.sortedarraylist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class SortedArrayListIterator<T> implements Iterator<T> {
    private final ArrayList<T> data;
    private int index;
    private int count;

    public SortedArrayListIterator(ArrayList<T> data, Comparator<T> comparator, T from, int count) {
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

