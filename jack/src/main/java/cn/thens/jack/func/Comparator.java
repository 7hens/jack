package cn.thens.jack.func;

/**
 * @author 7hens
 */
public interface Comparator<T> {
    int compare(T t1, T t2) throws Throwable;

    abstract class X<T> implements Comparator<T> {
        @Override
        public abstract int compare(T t1, T t2);

        public X<T> reversed() {
            return of((a, b) -> this.compare(b, a));
        }

        public X<T> then(java.util.Comparator<? super T> comparator) {
            X<T> source = this;
            return of((a, b) -> {
                int result = source.compare(a, b);
                return result != 0 ? result : comparator.compare(a, b);
            });
        }

        public X<T> thenComparing(java.util.Comparator<? super T> comparator) {
            return then(comparator);
        }

        @SuppressWarnings("unchecked")
        public static <T extends Comparable<?>> int compareValues(T a, T b) {
            if (a == b) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return ((Comparable<Object>) a).compareTo(b);
        }

        public static <T> X<T> of(Comparator<T> comparator) {
            return new X<T>() {
                @Override
                public int compare(T a, T b) {
                    try {
                        return comparator.compare(a, b);
                    } catch (Throwable e) {
                        throw ThrowableWrapper.of(e);
                    }
                }
            };
        }

        public static <T> X<T> by(Func1<? super T, ? extends Comparable<?>> selector) {
            return of((a, b) -> compareValues(selector.call(a), selector.call(b)));
        }

        public static <T, K> X<T> by(java.util.Comparator<? super K> comparator, Func1<T, ? extends K> selector) {
            return of((a, b) -> comparator.compare(selector.call(a), selector.call(b)));
        }
    }
}
