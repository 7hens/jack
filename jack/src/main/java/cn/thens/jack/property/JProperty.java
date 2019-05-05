package cn.thens.jack.property;

import cn.thens.jack.func.JAction;
import cn.thens.jack.func.JFunc;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class JProperty<T> {

    public JGetter<T> get(final JFunc.T0<T> getter) {
        return getter::call;
    }

    public JGetter<T> get(T value) {
        return get(() -> value);
    }

    public JSetter<T> set(final JAction.T1<T> setter, final JFunc.T0<T> getter) {
        return new JSetter<T>() {
            @Override
            public void set(T t) {
                setter.call(t);
            }

            @Override
            public T get() {
                return getter.call();
            }
        };
    }

    public JSetter<T> set(final JAction.T1<T> setter, final T value) {
        return set(setter, () -> value);
    }

    public SetterSupplier<T> set(final JAction.T1<T> setter) {
        return new SetterSupplier<>(this, setter);
    }

    public static class SetterSupplier<T> {
        private final JAction.T1<T> setter;
        private final JProperty<T> jProperty;

        SetterSupplier(JProperty<T> jProperty, JAction.T1<T> setter) {
            this.jProperty = jProperty;
            this.setter = setter;
        }

        public JSetter<T> get(JFunc.T0<T> getter) {
            return jProperty.set(setter, getter);
        }

        public JSetter<T> get(T value) {
            return jProperty.set(setter, value);
        }
    }
}
