package cn.thens.jack;

@SuppressWarnings({"WeakerAccess", "unused", "EqualsReplaceableByObjectsCall", "unchecked"})
public final class JOne<T> implements JGetter<T> {
    private final JGetter<T> getter;

    private JOne(JGetter<T> getter) {
        this.getter = getter;
    }

    private static final JOne EMPTY = of(null);

    public static <T> JOne<T> empty() {
        return EMPTY;
    }

    public static <T> JOne<T> of(JGetter<T> getter) {
        return new JOne<>(getter);
    }

    public static <T> JOne<T> of(T value) {
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
        if (o instanceof JOne) {
            return equals(get(), ((JOne) o).get());
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

    public JOne<T> elvis(T newValue) {
        return isNotNull() ? this : of(newValue);
    }

    public JOne<T> elvis(JGetter<T> getter) {
        return isNotNull() ? this : of(getter.get());
    }

    public boolean is(Class<?> clazz) {
        if (isNull()) return false;
        return clazz.isAssignableFrom(type());
    }

    public boolean isNot(Class<?> clazz) {
        return !is(clazz);
    }

    public <U> JOne<U> cast(Class<U> clazz) {
        if (is(clazz)) return of((JGetter<U>) this);
        throw new ClassCastException("expected " + clazz + ", but found " + type());
    }

    public <U> JOne<U> as(Class<U> clazz) {
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

    public final boolean in(Object... items) {
        T value = get();
        for (Object item : items) {
            if (equals(value, item)) {
                return true;
            }
        }
        return false;
    }

    public <U> JOne<U> set(U value) {
        return of(value);
    }

    public <U> JOne<U> let(JFunc.F1<T, U> func) {
        return of(func.call(get()));
    }

    public JOne<T> also(JFunc.A1<T> func) {
        func.run(get());
        return this;
    }

    public <U> U with(U value) {
        return value;
    }

    public JOne<T> apply(JFunc.A1<JOne<T>> func) {
        func.run(this);
        return this;
    }

    public <U> JOne<U> call(JFunc.F1<JOne<T>, U> func) {
        return of(func.call(this));
    }

    public <U> JOne<U> safeCall(JFunc.F1<JOne<T>, U> func) {
        return isNotNull() ? call(func) : empty();
    }

    public <U> JOne<U> catchError(JFunc.F1<JOne<T>, U> func) {
        try {
            return of(func.call(this));
        } catch (Throwable e) {
            return empty();
        }
    }
}
