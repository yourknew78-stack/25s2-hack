package sorteddata.sortedarraylist;

import sorteddata.SortedData;

import java.util.*;

public class SortedArrayList<T> extends SortedData<T> {
    private final Comparator<T> comparator;
    private final ArrayList<T> list;

    public SortedArrayList(Comparator<T> comparator) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public boolean insert(T value) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public T get(T value) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public Iterator<T> getRange(T start, int count, boolean backwards) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    private static Random random;

    public T getAtIndex(int i) {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    @Override
    public T getRandom() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public Iterator<T> getAll() {
        throw new UnsupportedOperationException("TODO: 待实现");
    }
}

