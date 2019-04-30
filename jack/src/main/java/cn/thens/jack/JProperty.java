package cn.thens.jack;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class JProperty<T> {

    public JGetter<T> get(final JFunc.F0<T> getter) {
        return getter::call;
    }

    public JGetter<T> get(T value) {
        return get(() -> value);
    }

    public JSetter<T> set(final JFunc.A1<T> setter, final JFunc.F0<T> getter) {
        return new JSetter<T>() {
            @Override
            public void set(T t) {
                setter.run(t);
            }

            @Override
            public T get() {
                return getter.call();
            }
        };
    }

    public JSetter<T> set(final JFunc.A1<T> setter, final T value) {
        return set(setter, () -> value);
    }

    public SetterSupplier<T> set(final JFunc.A1<T> setter) {
        return new SetterSupplier<>(this, setter);
    }

    public static class SetterSupplier<T> {
        private final JFunc.A1<T> setter;
        private final JProperty<T> jProperty;

        SetterSupplier(JProperty<T> jProperty, JFunc.A1<T> setter) {
            this.jProperty = jProperty;
            this.setter = setter;
        }

        public JSetter<T> get(JFunc.F0<T> getter) {
            return jProperty.set(setter, getter);
        }

        public JSetter<T> get(T value) {
            return jProperty.set(setter, value);
        }
    }
}
