package cn.thens.jack.func;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Once<R> {
    public static final int INITIALIZED = 0;
    public static final int STARTED = 1;
    public static final int ENDED = 2;

    private final AtomicInteger state = new AtomicInteger(INITIALIZED);
    private R result;

    private Once() {
    }

    public static <R> Once<R> create() {
        return new Once<>();
    }

    public R call(Func0<? extends R> func) {
        if (state.get() == ENDED) return result;
        if (state.compareAndSet(INITIALIZED, STARTED)) {
            try {
                result = func.invoke();
            } catch (Throwable e) {
                throw new ThrowableWrapper(e);
            } finally {
                state.set(ENDED);
            }
        }
        //noinspection StatementWithEmptyBody
        while (state.get() != ENDED) ;
        return result;
    }

    public void run(Action0 action) {
        call(() -> {
            action.run();
            return null;
        });
    }

    public int getState() {
        return state.get();
    }
}
