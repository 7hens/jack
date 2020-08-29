package cn.thens.jack.scheduler;

import java.util.concurrent.Future;

/**
 * @author 7hens
 */
public final class Cancellables {
    private Cancellables() {
    }

    public static Cancellable create() {
        return new CancellableImpl(false);
    }

    private static Cancellable CANCELLED = new CancellableImpl(true);

    public static Cancellable cancelled() {
        return CANCELLED;
    }

    private static Cancellable NEVER = new CancellableNever();

    public static Cancellable never() {
        return NEVER;
    }

    public static Cancellable single() {
        return new CancellableSingle();
    }

    public static Cancellable reusable() {
        return new CancellableReusable();
    }

    public static Cancellable of(final ICancellable cancellable) {
        if (cancellable instanceof Cancellable) {
            return (Cancellable) cancellable;
        }
        return new CancellableImpl(false) {
            @Override
            protected void onCancel() {
                super.onCancel();
                cancellable.cancel();
            }
        };
    }

    public static Cancellable from(Future<?> future) {
        if (future instanceof Cancellable) {
            return (Cancellable) future;
        }
        return new CancellableFuture<>(future);
    }

}
