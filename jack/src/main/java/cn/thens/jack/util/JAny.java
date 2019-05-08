package cn.thens.jack.util;

import cn.thens.jack.func.JAction1;
import cn.thens.jack.func.JFunc1;
import cn.thens.jack.property.JGetter;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked", "EqualsReplaceableByObjectsCall"})
public abstract class JAny<T> implements JGetter<T> {
    private static final JAny EMPTY = of(null);

    public static <T> JAny<T> empty() {
        return EMPTY;
    }

    public static <T> JAny<T> of(JGetter<T> getter) {
        return new JAny<T>() {
            @Override
            public T get() {
                return getter.get();
            }
        };
    }

    public static <T> JAny<T> of(T value) {
        return of(() -> value);
    }

    public Class<?> type() {
        return get().getClass();
    }

    public static boolean equals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof JAny) {
            return equals(get(), ((JAny) o).get());
        }
        return equals(get(), o);
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

    public JAny<T> elvis(T newValue) {
        return isNotNull() ? this : of(newValue);
    }

    public JAny<T> elvis(JGetter<T> getter) {
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
        if (is(clazz)) return of((JGetter<U>) this);
        throw new ClassCastException("expected " + clazz + ", but found " + type());
    }

    public <U> JAny<U> as(Class<U> clazz) {
        if (is(clazz)) return of((JGetter<U>) this);
        return empty();
    }

    public boolean in(Iterable<T> iterable) {
        T value = get();
        for (T item : iterable) {
            if (equals(value, item)) {
                return true;
            }
        }
        return false;
    }

    public boolean in(Object... items) {
        T value = get();
        for (Object item : items) {
            if (equals(value, item)) {
                return true;
            }
        }
        return false;
    }

    public <U> JAny<U> set(U value) {
        return of(value);
    }

    public <U> JAny<U> let(JFunc1<T, U> func) {
        return of(func.invoke(get()));
    }

    public JAny<T> also(JAction1<T> func) {
        func.invoke(get());
        return this;
    }

    public <U> U with(U value) {
        return value;
    }

    public JAny<T> apply(JAction1<JAny<T>> func) {
        func.invoke(this);
        return this;
    }

    public <U> JAny<U> call(JFunc1<JAny<T>, U> func) {
        return of(func.invoke(this));
    }

    public <U> JAny<U> safeCall(JFunc1<JAny<T>, U> func) {
        return isNotNull() ? call(func) : empty();
    }

    public <U> JAny<U> catchError(JFunc1<JAny<T>, U> func) {
        try {
            return of(func.invoke(this));
        } catch (Throwable e) {
            return empty();
        }
    }
}
