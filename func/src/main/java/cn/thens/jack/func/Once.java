package cn.thens.jack.func;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused"})
public final class Once<R> {
    public static final int INITIALIZED = 0;
    public static final int STARTED = 1;
    public static final int ENDED = 2;

    private final AtomicInteger state = new AtomicInteger(INITIALIZED);
    private volatile R result;

    private Once() {
    }

    public static <R> Once<R> create() {
        return new Once<>();
    }

    public R call(Func0<? extends R> func) {
        if (state.get() == ENDED) return result;
        if (state.compareAndSet(INITIALIZED, STARTED)) {
            try {
                result = func.call();
            } catch (Throwable e) {
                throw Values.wrap(e);
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

    public int state() {
        return state.get();
    }
}
