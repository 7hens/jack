package cn.thens.jack.func;

/**
 * @author 7hens
 */
public final class Lazy<T> {
    private final Func0<? extends T> factory;
    private final Once<T> once = Once.create();

    private Lazy(Func0<? extends T> factory) {
        this.factory = factory;
    }

    public T get() {
        return once.call(factory);
    }

    public boolean isInitialized() {
        return once.state() >= Once.STARTED;
    }

    public static <T> Lazy<T> of(Func0<? extends T> factory) {
        return new Lazy<>(factory);
    }
}
