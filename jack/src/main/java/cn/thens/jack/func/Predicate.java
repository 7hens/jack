package cn.thens.jack.func;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 7hens
 */
public interface Predicate<T> {
    boolean test(T t) throws Throwable;

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
                        throw ThrowableWrapper.of(throwable);
                    }
                }
            };
        }

        public static <T> X<T> of(Func0<? extends Boolean> func) {
            return of(it -> func.call());
        }

        public static <T> X<T> eq(T value) {
            return of(it -> equals(it, value));
        }

        public static <T> X<T> take(int count) {
            final AtomicInteger restCount = new AtomicInteger(count);
            return of(it -> restCount.decrementAndGet() >= 0);
        }

        public static <T> X<T> skip(int count) {
            final AtomicInteger restCount = new AtomicInteger(count);
            return of(it -> restCount.decrementAndGet() < 0);
        }

        private static boolean equals(Object a, Object b) {
            //noinspection EqualsReplaceableByObjectsCall
            return a != null ? a.equals(b) : b == null;
        }
    }
}
