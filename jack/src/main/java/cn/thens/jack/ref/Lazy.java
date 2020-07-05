package cn.thens.jack.ref;

import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Once;

@SuppressWarnings({"unused"})
public final class Lazy<T> implements Getter<T> {
    private Once<T> once = Once.create();
    private final Func0<? extends T> creator;

    private Lazy(Func0<? extends T> creator) {
        this.creator = creator;
    }

    @Override
    public T get() {
        return once.call(creator);
    }

    public boolean isInitialized() {
        return once.getState() == Once.ENDED;
    }

    public static <T> Lazy<T> of(final Func0<? extends T> creator) {
        return new Lazy<>(creator);
    }
}
