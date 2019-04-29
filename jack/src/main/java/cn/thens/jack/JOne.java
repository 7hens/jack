package cn.thens.jack;

@SuppressWarnings({"WeakerAccess", "unused", "EqualsReplaceableByObjectsCall"})
public final class JOne<T> implements JGetter<T> {
    private final JGetter<T> getter;

    protected JOne(JGetter<T> getter) {
        this.getter = getter;
    }

    @Override
    public T get() {
        return getter.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof JGetter) {
            return equals(get(), ((JOne) o).get());
        }
        return equals(get(), o);
    }

    @Override
    public int hashCode() {
        T value = get();
        return value != null ? value.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        T value = get();
        return value == null ? "null" : value.toString();
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

    public boolean isNotNull() {
        return get() != null;
    }

    public boolean isNull() {
        return get() == null;
    }

    public JOne<T> elvis(T newValue) {
        return isNotNull() ? this : of(newValue);
    }

    public JOne<T> elvis(JFunc.F0<T> func) {
        return isNotNull() ? this : of(func.invoke());
    }

    public <U> JOne<U> safe(JFunc.F1<JOne<T>, JOne<U>> func) {
        return isNotNull() ? func.invoke(this) : of(null);
    }

    public boolean is(Class<?> clazz) {
        if (isNull()) return false;
        return clazz.isAssignableFrom(get().getClass());
    }

    @SuppressWarnings("unchecked")
    public <U> JOne<U> as(Class<U> clazz) {
        if (is(clazz)) return of((JGetter<U>) this);
        return of(null);
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

    public <U> JOne<U> map(JFunc.F1<T, U> func) {
        return of(func.invoke(get()));
    }

    public <U> JOne<U> map(U value) {
        return of(value);
    }

    public <U> JOne<U> flatMap(JFunc.F1<T, JOne<U>> func) {
        return func.invoke(get());
    }

    public <U> JOne<U> flatMap(JOne<U> one) {
        return one;
    }

    public JOne<T> also(JFunc.A1<T> func) {
        func.call(get());
        return this;
    }

    public JOne<T> apply(JFunc.A1<JOne<T>> func) {
        func.call(this);
        return this;
    }

    public <U> U with(JFunc.F1<JOne<T>, U> func) {
        return func.invoke(this);
    }
}
