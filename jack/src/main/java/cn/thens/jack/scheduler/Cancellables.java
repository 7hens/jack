package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
public final class Cancellables {
    public static Cancellable create() {
        return new CancellableImpl(false);
    }

    public static Cancellable of(final ICancellable cancellable) {
        return new CancellableImpl(false) {
            @Override
            protected void onCancel() {
                super.onCancel();
                cancellable.cancel();
            }
        };
    }

    private static Cancellable CANCELLED = new CancellableImpl(true);

    public static Cancellable cancelled() {
        return CANCELLED;
    }
}
