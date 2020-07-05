package cn.thens.jack.util;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Functions;
import cn.thens.jack.ref.Getter;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked", "EqualsReplaceableByObjectsCall"})
public abstract class JAny<T> implements Getter<T> {

    private static final JAny EMPTY = new JAny() {
        @Override
        public Object get() {
            return null;
        }
    };

    public static <T> JAny<T> empty() {
        return EMPTY;
    }

    public static <T> JAny<T> of(final T value) {
        if (value == null) return empty();
        return new JAny<T>() {
            @Override
            public T get() {
                return value;
            }
        };
    }

    public Class<?> type() {
        return get().getClass();
    }

    private static boolean equals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        Object thatValue = that;
        if (that instanceof JAny) {
            thatValue = ((JAny) that).get();
        }
        return equals(get(), thatValue);
    }

    @Override
    public int hashCode() {
        return isNotNull() ? get().hashCode() : 0;
    }

    @Override
    public String toString() {
        return isNull() ? "null" : get().toString();
    }

    public boolean isNotNull() {
        return get() != null;
    }

    public boolean isNull() {
        return get() == null;
    }

    public JAny<T> requireNotNull() {
        JContract.requireNotNull(get());
        return this;
    }

    public JAny<T> elvis(T newValue) {
        return elvis(of(newValue));
    }

    public JAny<T> elvis(Getter<? extends T> getter) {
        return isNotNull() ? this : of(getter.get());
    }

    public boolean is(Class<?> clazz) {
        if (isNull()) return false;
        return clazz.isAssignableFrom(type());
    }

    public boolean isNot(Class<?> clazz) {
        return !is(clazz);
    }

    public <U> JAny<U> cast(Class<U> clazz) {
        if (is(clazz)) return (JAny<U>) this;
        throw new ClassCastException("expected " + clazz + ", but found " + type());
    }

    public <U> JAny<U> safeCast(Class<U> clazz) {
        if (is(clazz)) return (JAny<U>) this;
        return empty();
    }

    public boolean in(Iterable<T> iterable) {
        for (T item : iterable) {
            if (equals(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean in(Object... items) {
        return in((Iterable) JSequence.of(items));
    }

    public <U> JAny<U> set(U value) {
        return of(value);
    }

    public <U> U with(U value) {
        return value;
    }

    public JAny<T> also(Action1<T> func) {
        Functions.of(func).run(get());
        return this;
    }

    public JAny<T> apply(Action1<JAny<T>> func) {
        Functions.of(func).run(this);
        return this;
    }

    public <U> JAny<U> call(Func1<T, ? extends U> func) {
        return of(func.invoke(get()));
    }

    public <U> JAny<U> safeCall(Func1<T, ? extends U> func) {
        return isNotNull() ? call(func) : empty();
    }

    public <U> JAny<U> catchError(Func1<T, ? extends U> func, Func1<Throwable, ? extends U> defaultValue) {
        try {
            return call(func);
        } catch (Throwable e) {
            return of(e).call(defaultValue);
        }
    }

    public <U> JAny<U> catchError(Func1<T, U> func) {
        return catchError(func, Functions.empty());
    }
}
