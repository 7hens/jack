package cn.thens.jack.func;

/**
 * @author 7hens
 */
public interface Comparator<T> {
    int compare(T t1, T t2) throws Throwable;

    abstract class X<T> implements Comparator<T>, java.util.Comparator<T>, Func2<T, T, Integer> {
        @Override
        public abstract int compare(T t1, T t2);

        @Override
        public Integer call(T t1, T t2) {
            return compare(t1, t2);
        }

        public Func2.X<T, T, Integer> func() {
            return Func2.X.of(this);
        }

        public Comparator.X<T> reversed() {
            return of((a, b) -> this.compare(b, a));
        }

        public Comparator.X<T> then(Comparator<? super T> comparator) {
            Comparator.X<T> source = this;
            return of((a, b) -> {
                int result = source.compare(a, b);
                return result != 0 ? result : comparator.compare(a, b);
            });
        }

        public Comparator.X<T> thenComparing(Comparator<? super T> comparator) {
            return then(comparator);
        }

        @SuppressWarnings("unchecked")
        public static <T extends Comparable<?>> int compareValues(T a, T b) {
            if (a == b) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return ((Comparable<Object>) a).compareTo(b);
        }

        public static <T> Comparator.X<T> of(Comparator<T> comparator) {
            return new Comparator.X<T>() {
                @Override
                public int compare(T a, T b) {
                    try {
                        return comparator.compare(a, b);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }

        public static <T> Comparator.X<T> by(Func1<? super T, ? extends Comparable<?>> selector) {
            return of((a, b) -> compareValues(selector.call(a), selector.call(b)));
        }

        public static <T, K> Comparator.X<T> by(Comparator<? super K> comparator, Func1<T, ? extends K> selector) {
            return of((a, b) -> comparator.compare(selector.call(a), selector.call(b)));
        }

        public static <T extends Comparable<T>> Comparator.X<T> self() {
            return of((a, b) -> {
                if (a == b) return 0;
                if (a == null) return -1;
                if (b == null) return 1;
                return a.compareTo(b);
            });
        }
    }
}
