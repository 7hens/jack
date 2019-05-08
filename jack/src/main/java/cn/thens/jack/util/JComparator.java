package cn.thens.jack.util;

import java.util.Comparator;

import cn.thens.jack.func.JFunc1;

@SuppressWarnings({"WeakerAccess"})
public abstract class JComparator<T> implements Comparator<T> {
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<?>> int compareValues(T a, T b) {
        if (a == b) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return ((Comparable<Object>) a).compareTo(b);
    }

    public static <T> JComparator<T> of(Comparator<T> comparator) {
        return new JComparator<T>() {
            @Override
            public int compare(T a, T b) {
                return comparator.compare(a, b);
            }
        };
    }

    public static <T> JComparator<T> by(JFunc1<T, ? extends Comparable<?>> selector) {
        return new JComparator<T>() {
            @Override
            public int compare(T a, T b) {
                return compareValues(selector.invoke(a), selector.invoke(b));
            }
        };
    }

    public static <T, K> JComparator<T> by(Comparator<? super K> comparator, JFunc1<T, ? extends K> selector) {
        return new JComparator<T>() {
            @Override
            public int compare(T a, T b) {
                return comparator.compare(selector.invoke(a), selector.invoke(b));
            }
        };
    }

    public JComparator<T> reversed() {
        JComparator<T> source = this;
        return new JComparator<T>() {
            @Override
            public int compare(T a, T b) {
                return source.compare(b, a);
            }
        };
    }

    public JComparator<T> then(Comparator<? super T> comparator) {
        JComparator<T> source = this;
        JContract.requireNotNull(comparator);
        return new JComparator<T>() {
            @Override
            public int compare(T a, T b) {
                int result = source.compare(a, b);
                return result != 0 ? result : comparator.compare(a, b);
            }
        };
    }

    public JComparator<T> thenComparing(Comparator<? super T> comparator) {
        return then(comparator);
    }
}
