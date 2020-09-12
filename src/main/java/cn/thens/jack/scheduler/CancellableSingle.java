package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
class CancellableSingle implements Cancellable {
    private Cancellable lastCancellable = Cancellables.cancelled();

    @Override
    public void addCancellable(ICancellable onCancel) {
        lastCancellable.cancel();
        lastCancellable = Cancellables.of(onCancel);
    }

    @Override
    public boolean isCancelled() {
        return lastCancellable.isCancelled();
    }

    @Override
    public void cancel() {
        lastCancellable.cancel();
    }
}
