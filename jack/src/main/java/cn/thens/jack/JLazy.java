package cn.thens.jack;

import java.lang.reflect.Constructor;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class JLazy<T> implements JGetter<T> {
    private final JFunc.P0<? extends T> creator;

    private JLazy(JFunc.P0<? extends T> creator) {
        this.creator = creator;
    }

    private JOnce<T> once = new JOnce<>();

    @Override
    public T get() {
        return once.invoke(creator);
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

    public static <T> JLazy<T> create(final JFunc.P0<? extends T> creator) {
        return new JLazy<>(creator);
    }
}
