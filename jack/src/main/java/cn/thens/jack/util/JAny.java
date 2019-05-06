package cn.thens.jack.util;

import cn.thens.jack.func.JAction;
import cn.thens.jack.func.JFunc;
import cn.thens.jack.property.JGetter;

@SuppressWarnings({"WeakerAccess", "unused", "EqualsReplaceableByObjectsCall", "unchecked"})
public final class JAny<T> implements JGetter<T> {
    private final JGetter<T> getter;

    private JAny(JGetter<T> getter) {
        this.getter = getter;
    }

    private static final JAny EMPTY = of(null);

    public static <T> JAny<T> empty() {
        return EMPTY;
    }

    public static <T> JAny<T> of(JGetter<T> getter) {
        return new JAny<>(getter);
    }

    public static <T> JAny<T> of(T value) {
        return of(() -> value);
    }

    public static boolean equals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public T get() {
        return getter.get();
    }

    public Class<?> type() {
        return get().getClass();
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

    public <U> JAny<U> let(JFunc.T1<T, U> func) {
        return of(func.call(get()));
    }

    public JAny<T> also(JAction.T1<T> func) {
        func.call(get());
        return this;
    }

    public <U> U with(U value) {
        return value;
    }

    public JAny<T> apply(JAction.T1<JAny<T>> func) {
        func.call(this);
        return this;
    }

    public <U> JAny<U> call(JFunc.T1<JAny<T>, U> func) {
        return of(func.call(this));
    }

    public <U> JAny<U> safeCall(JFunc.T1<JAny<T>, U> func) {
        return isNotNull() ? call(func) : empty();
    }

    public <U> JAny<U> catchError(JFunc.T1<JAny<T>, U> func) {
        try {
            return of(func.call(this));
        } catch (Throwable e) {
            return empty();
        }
    }
}
