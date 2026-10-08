package sorteddata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;

/**
 * A SortedData implementation backed by a TreeMap, giving O(log n) insert,
 * lookup and removal. Unlike SortedArrayList it stays fast when hundreds of
 * thousands of elements are stored, so it is used for the reaction storage.
 * Additionally supports removing individual elements via {@link #remove}.
 */
public class TreeMapSortedData<T> extends SortedData<T> {
    private final TreeMap<T, T> map;

    public TreeMapSortedData(Comparator<T> comparator) {
        this.map = new TreeMap<>(comparator);
    }

    @Override
    public boolean insert(T value) {
        return map.putIfAbsent(value, value) == null;
    }

    /**
     * Removes the element equal (per the comparator) to the given value.
     * @return true if an element was removed, false otherwise
     */
    public boolean remove(T value) {
        return map.remove(value) != null;
    }

    @Override
    public T get(T value) {
        return map.get(value);
    }

    @Override
    public T getAtIndex(int i) {
        if (i < 0 || i >= map.size()) throw new IndexOutOfBoundsException(i);
        Iterator<T> it = map.keySet().iterator();
        for (int j = 0; j < i; j++) it.next();
        return it.next();
    }

    @Override
    public Iterator<T> getRange(T start, int count, boolean backwards) {
        final Iterator<T> base;
        if (backwards) {
            base = (start == null ? map.descendingMap() : map.headMap(start, true).descendingMap())
                    .keySet().iterator();
        } else {
            base = (start == null ? map : map.tailMap(start, true)).keySet().iterator();
        }
        return new Iterator<>() {
            private int remaining = count;

            @Override
            public boolean hasNext() {
                return remaining != 0 && base.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                remaining--;
                return base.next();
            }
        };
    }

    private static final Random random = new Random();

    @Override
    public T getRandom() {
        if (map.isEmpty()) return null;
        return getAtIndex(random.nextInt(map.size()));
    }
}
