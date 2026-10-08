package sorteddata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A SortedData implementation backed by a HashMap, giving O(1) insert, lookup
 * and removal. Sorted order is produced lazily: a sorted snapshot is computed
 * only when an ordered view is requested (iteration, index or random access)
 * and is cached until the next mutation. Suited to write-heavy workloads such
 * as the reaction storage, where a million inserts must complete in a second.
 * Additionally supports removing individual elements via {@link #remove}.
 */
public class LazySortedData<T> extends SortedData<T> {
    private final Comparator<T> comparator;
    private final HashMap<T, T> map = new HashMap<>();
    private ArrayList<T> sortedCache = null;

    public LazySortedData(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean insert(T value) {
        if (map.putIfAbsent(value, value) != null) return false;
        sortedCache = null;
        return true;
    }

    /**
     * Removes the element equal (per the comparator) to the given value.
     * @return true if an element was removed, false otherwise
     */
    public boolean remove(T value) {
        if (map.remove(value) == null) return false;
        sortedCache = null;
        return true;
    }

    @Override
    public T get(T value) {
        return map.get(value);
    }

    private ArrayList<T> sorted() {
        if (sortedCache == null) {
            sortedCache = new ArrayList<>(map.values());
            sortedCache.sort(comparator);
        }
        return sortedCache;
    }

    @Override
    public T getAtIndex(int i) {
        return sorted().get(i);
    }

    @Override
    public Iterator<T> getRange(T start, int count, boolean backwards) {
        List<T> sorted = sorted();
        int index;
        if (start == null) {
            index = backwards ? sorted.size() - 1 : 0;
        } else {
            int pos = Collections.binarySearch(sorted, start, comparator);
            if (backwards) index = pos >= 0 ? pos : -pos - 2;
            else index = pos >= 0 ? pos : -pos - 1;
        }
        final int from = index;
        return new Iterator<>() {
            private int i = from;
            private int remaining = count;

            @Override
            public boolean hasNext() {
                return remaining != 0 && i >= 0 && i < sorted.size();
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                remaining--;
                return sorted.get(backwards ? i-- : i++);
            }
        };
    }

    private static final Random random = new Random();

    @Override
    public T getRandom() {
        if (map.isEmpty()) return null;
        return sorted().get(random.nextInt(map.size()));
    }
}
