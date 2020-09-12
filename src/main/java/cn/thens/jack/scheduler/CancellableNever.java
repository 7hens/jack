package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
final class CancellableNever implements Cancellable {
    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
    }

    @Override
    public void into(Cancellable cancellable) {
    }

    @Override
    public void cancel() {
    }
}
