package cn.thens.jack.scheduler;

import java.util.concurrent.Future;

/**
 * @author 7hens
 */
public final class Cancellables {
    public static Cancellable create() {
        return new CancellableImpl(false);
    }

    private static Cancellable CANCELLED = new CancellableImpl(true);

    public static Cancellable cancelled() {
        return CANCELLED;
    }

    public static Cancellable single() {
        return new Cancellable() {
            private Cancellable lastCancellable = cancelled();

            @Override
            public void addCancellable(ICancellable onCancel) {
                lastCancellable.cancel();
                lastCancellable = of(onCancel);
            }

            @Override
            public boolean isCancelled() {
                return lastCancellable.isCancelled();
            }

            @Override
            public void cancel() {
                lastCancellable.cancel();
            }
        };
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
