package cn.thens.jack.func;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import cn.thens.jack.ref.Ref;

/**
 * @author 7hens
 */
public interface Predicate<T> {
    boolean test(T t) throws Throwable;

    @SuppressWarnings("rawtypes")
    abstract class X<T> implements Predicate<T> {
        @Override
        public abstract boolean test(T t);

        public X<T> not() {
            return of(it -> !test(it));
        }

        public X<T> and(Predicate<? super T> other) {
            return of(it -> test(it) && other.test(it));
        }

        public X<T> or(Predicate<? super T> other) {
            return of(it -> test(it) || other.test(it));
        }

        public X<T> xor(Predicate<? super T> other) {
            return of(it -> {
                boolean selfTest = test(it);
                boolean otherTest = other.test(it);
                return selfTest && !otherTest || !selfTest && otherTest;
            });
        }

        private static final X ALWAYS_TRUE = new X() {
            @Override
            public boolean test(Object o) {
                return true;
            }
        };

        private static final X ALWAYS_FALSE = new X() {
            @Override
            public boolean test(Object o) {
                return false;
            }
        };

        @SuppressWarnings("unchecked")
        public static <T> X<T> alwaysTrue() {
            return ALWAYS_TRUE;
        }

        @SuppressWarnings("unchecked")
        public static <T> X<T> alwaysFalse() {
            return ALWAYS_FALSE;
        }

        public static <T> X<T> of(Predicate<? super T> predicate) {
            return new X<T>() {
                @Override
                public boolean test(T t) {
                    try {
                        return predicate.test(t);
                    } catch (Throwable throwable) {
                        throw Exceptions.wrap(throwable);
                    }
                }
            };
        }

        public static <T> X<T> eq(T value) {
            return of(it -> Ref.of(it).equals(value));
        }

        public static <T> X<T> take(int count) {
            final AtomicInteger restCount = new AtomicInteger(count);
            return of(it -> restCount.decrementAndGet() >= 0);
        }

        public static <T> X<T> skip(int count) {
            final AtomicInteger restCount = new AtomicInteger(count);
            return of(it -> restCount.decrementAndGet() < 0);
        }

        public static <T, K> X<T> distinctBy(Func1<? super T, ? extends K> keySelector) {
            final HashSet<K> observed = new HashSet<>();
            return of(it -> {
                K key = keySelector.call(it);
                return observed.add(key);
            });
        }

        public static <T> X<T> distinct() {
            return distinctBy(Funcs.self());
        }

        public static <T, K> X<T> distinctUntilChangedBy(Func1<? super T, ? extends K> keySelector) {
            return of(new Predicate<T>() {
                K lastKey = null;

                @Override
                public boolean test(T t) throws Throwable {
                    K key = keySelector.call(t);
                    K prevKey = lastKey;
                    lastKey = key;
                    return Ref.of(key).equals(prevKey);
                }
            });
        }

        public static <T> X<T> distinctUntilChanged() {
            return distinctUntilChangedBy(Funcs.self());
        }
    }
}
