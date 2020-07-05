package cn.thens.jack.ref;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Functions;

@SuppressWarnings({"unused"})
public class Property<T> implements Setter<T> {

    @Override
    public void set(T t) {
    }

    @Override
    public T get() {
        return null;
    }

    private static <T> Property<T>
    of(final Func0<? extends T> getter, final Action1<? super T> setter) {
        Func0.X<? extends T> get = Functions.of(getter);
        Action1.X<? super T> set = Functions.of(setter);
        return new Property<T>() {
            @Override
            public void set(T t) {
                set.run(t);
            }

            @Override
            public T get() {
                return get.invoke();
            }
        };
    }

    public final Property<T> get(final Func0<T> getter) {
        return of(getter, this::set);
    }

    public final Property<T> get(T value) {
        return get(() -> value);
    }

    public final Property<T> set(final Action1<? super T> setter) {
        return of(this::get, setter);
    }
}
