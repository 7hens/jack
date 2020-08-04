package cn.thens.jack.func;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 7hens
 */
public interface Predicate<T> {
    boolean test(T t) throws Throwable;

    @SuppressWarnings("rawtypes")
    abstract class X<T> implements Predicate<T>, Func1<T, Boolean> {
        @Override
        public abstract boolean test(T t);

        @Override
        public Boolean call(T t) {
            return test(t);
        }

        public Func1.X<T, Boolean> func() {
            return Func1.X.of(this);
        }

        public Predicate.X<T> not() {
            return of(it -> !test(it));
        }

        public Predicate.X<T> negate() {
            return not();
        }

        public Predicate.X<T> and(Predicate<? super T> other) {
            return of(it -> test(it) && other.test(it));
        }

        public Predicate.X<T> or(Predicate<? super T> other) {
            return of(it -> test(it) || other.test(it));
        }

        public Predicate.X<T> xor(Predicate<? super T> other) {
            return of(it -> {
                boolean selfTest = test(it);
                boolean otherTest = other.test(it);
                return selfTest && !otherTest || !selfTest && otherTest;
            });
        }

        private static final Predicate.X ALWAYS_TRUE = new Predicate.X() {
            @Override
            public boolean test(Object o) {
                return true;
            }
        };

        private static final Predicate.X ALWAYS_FALSE = new Predicate.X() {
            @Override
            public boolean test(Object o) {
                return false;
            }
        };

        @SuppressWarnings("unchecked")
        public static <T> Predicate.X<T> alwaysTrue() {
            return ALWAYS_TRUE;
        }

        @SuppressWarnings("unchecked")
        public static <T> Predicate.X<T> alwaysFalse() {
            return ALWAYS_FALSE;
        }

        public static <T> Predicate.X<T> of(Predicate<? super T> predicate) {
            return new Predicate.X<T>() {
                @Override
                public boolean test(T t) {
                    try {
                        return predicate.test(t);
                    } catch (Throwable throwable) {
                        throw Things.wrap(throwable);
                    }
                }
            };
        }

        public static <T> Predicate.X<T> eq(T value) {
            return of(it -> Things.equals(it, value));
        }

        public static <T> Predicate.X<T> take(int count) {
            final AtomicInteger restCount = new AtomicInteger(count);
            return of(it -> restCount.decrementAndGet() >= 0);
        }

        public static <T> Predicate.X<T> skip(int count) {
            final AtomicInteger restCount = new AtomicInteger(count);
            return of(it -> restCount.decrementAndGet() < 0);
        }

        @Deprecated
        public static <T, K> Predicate.X<T> distinctBy(Func1<? super T, ? extends K> keySelector) {
            final HashSet<K> observed = new HashSet<>();
            return of(it -> {
                K key = keySelector.call(it);
                return observed.add(key);
            });
        }

        @Deprecated
        public static <T> Predicate.X<T> distinct() {
            return distinctBy(Funcs.self());
        }

        @Deprecated
        public static <T, K> Predicate.X<T> distinctUntilChangedBy(Func1<? super T, ? extends K> keySelector) {
            return of(new Predicate<T>() {
                Object lastKey = new Object();

                @Override
                public boolean test(T t) throws Throwable {
                    K key = keySelector.call(t);
                    Object prevKey = lastKey;
                    lastKey = key;
                    return Things.equals(key, prevKey);
                }
            });
        }

        @Deprecated
        public static <T> Predicate.X<T> distinctUntilChanged() {
            return distinctUntilChangedBy(Funcs.self());
        }
    }
}
