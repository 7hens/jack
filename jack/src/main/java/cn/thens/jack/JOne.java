package cn.thens.jack;

@SuppressWarnings({"WeakerAccess", "unused"})
public class JOne<T> implements JGetter<T> {

    private final JGetter<T> getter;

    protected JOne(JGetter<T> getter) {
        this.getter = getter;
    }

    @Override
    public T get() {
        return getter.get();
    }

    public static <T> JOne<T> of(JGetter<T> getter) {
        return new JOne<>(getter);
    }

    public static <T> JOne<T> of(T value) {
        return of(() -> value);
    }

    public boolean isNotNull() {
        return get() != null;
    }

    public boolean isNull() {
        return get() == null;
    }

    public boolean is(Class<?> clazz) {
        if (isNull()) return true;
        return clazz.isAssignableFrom(get().getClass());
    }

    public JOne<T> elvis(T newValue) {
        return isNotNull() ? this : of(newValue);
    }

    public JOne<T> elvis(JFunc.P0<T> func) {
        return isNotNull() ? this : of(func.invoke());
    }

    public <U> JOne<U> map(JFunc.P1<T, U> func) {
        return of(func.invoke(get()));
    }

    public <U> JOne<U> map(U value) {
        return of(value);
    }

    public <U> JOne<U> flatMap(JFunc.P1<T, JOne<U>> func) {
        return func.invoke(get());
    }

    public <U> JOne<U> flatMap(JOne<U> value) {
        return value;
    }

    public JOne<T> also(JAction.P1<T> func) {
        func.invoke(get());
        return this;
    }

    @SuppressWarnings("unchecked")
    public <U> JOne<U> cast(Class<U> clazz) {
        if (is(clazz)) return of((JGetter<U>) this);
        throw new ClassCastException();
    }

    public JOne<T> unsafe() {
        return of(this);
    }

    public JOne<T> safe() {
        return new JOne<T>(this) {
            @Override
            public <U> JOne<U> map(JFunc.P1<T, U> func) {
                return safeNotNull(() -> super.map(func));
            }

            @Override
            public <U> JOne<U> map(U value) {
                return safeNotNull(() -> super.map(value));
            }

            @Override
            public <U> JOne<U> flatMap(JFunc.P1<T, JOne<U>> func) {
                return safeNotNull(() -> super.flatMap(func));
            }

            @Override
            public <U> JOne<U> flatMap(JOne<U> value) {
                return safeNotNull(() -> super.flatMap(value));
            }

            @Override
            public JOne<T> also(JAction.P1<T> func) {
                return safeNotNull(() -> super.also(func));
            }

            @SuppressWarnings("unchecked")
            @Override
            public <U> JOne<U> cast(Class<U> clazz) {
                return is(clazz) ? of((U) clazz).safe() : safeNull();
            }

            private <U> JOne<U> safeNotNull(JFunc.P0<JOne<U>> func) {
                return isNotNull() ? func.invoke().safe() : safeNull();
            }

            private <U> JOne<U> safeNull() {
                return JOne.<U>of(null).safe();
            }
        };
    }
}
