package cn.thens.jack.property;

import java.lang.reflect.Constructor;

import cn.thens.jack.func.JFunc;
import cn.thens.jack.func.JOnce;

@SuppressWarnings({"unused"})
public final class JLazy<T> implements JGetter<T> {
    private final JFunc.T0<? extends T> creator;

    private JLazy(JFunc.T0<? extends T> creator) {
        this.creator = creator;
    }

    private JOnce.Func<T> once = JOnce.func();

    @Override
    public T get() {
        return once.call(creator);
    }

    public static <T> JLazy<T> create(final Class<? extends T> clazz) {
        return create(() -> {
            try {
                Constructor<? extends T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> JLazy<T> create(final JFunc.T0<? extends T> creator) {
        return new JLazy<>(creator);
    }
}
