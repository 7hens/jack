package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
final class CancellableEmpty implements Cancellable {
    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
    }

    @Override
    public void cancel() {
    }
}
