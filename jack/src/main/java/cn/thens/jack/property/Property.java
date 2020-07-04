package cn.thens.jack.property;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Functions;
import cn.thens.jack.util.ThrowableWrapper;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Property<T> implements Setter<T> {
    public static <T> Property<T> of(final Action1<T> setter, final Func0<T> getter) {
        return new Property<T>() {
            @Override
            public void set(T t) {
                try {
                    setter.run(t);
                } catch (Throwable throwable) {
                    throw new ThrowableWrapper(throwable);
                }
            }

            @Override
            public T get() {
                return Functions.of(getter).invoke();
            }
        };
    }

    public Property<T> get(final Func0<T> getter) {
        Property<T > self = this;
        return () -> Functions.of(getter).invoke();
    }

    public Getter<T> get(T value) {
        return () -> value;
    }

    public Setter<T> set(final Action1<T> setter, final Func0<T> getter) {
        return new Setter<T>() {
            @Override
            public void set(T t) {
                setter.run(t);
            }

            @Override
            public T get() {
                return getter.invoke();
            }
        };
    }

    public Setter<T> set(final Action1<T> setter, final T value) {
        return set(setter, () -> value);
    }

    public SetterSupplier<T> set(final Action1<T> setter) {
        return new SetterSupplier<>(this, setter);
    }

    public static class SetterSupplier<T> {
        private final Action1<T> setter;
        private final Property<T> jProperty;

        SetterSupplier(Property<T> jProperty, Action1<T> setter) {
            this.jProperty = jProperty;
            this.setter = setter;
        }

        public Setter<T> get(Func0<T> getter) {
            return jProperty.set(setter, getter);
        }

        public Setter<T> get(T value) {
            return jProperty.set(setter, value);
        }
    }
}
