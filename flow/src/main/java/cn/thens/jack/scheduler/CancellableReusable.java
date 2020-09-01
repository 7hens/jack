package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
final class CancellableReusable implements Cancellable {
    private Cancellable cancellable = Cancellables.create();

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        if (isCancelled()) {
            synchronized (this) {
                if (isCancelled()) {
                    cancellable = Cancellables.create();
                }
            }
        }
        cancellable.addCancellable(onCancel);
    }

    @Override
    public void into(Cancellable cancellable) {
        cancellable.addCancellable(this);
    }

    @Override
    public void cancel() {
        cancellable.cancel();
    }
}
