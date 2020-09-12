package cn.thens.jack.scheduler;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 7hens
 */
class CancellableSingle implements Cancellable {
    private AtomicReference<Cancellable> lastRef = new AtomicReference<>(Cancellables.cancelled());

    @Override
    public void addCancellable(ICancellable onCancel) {
        Cancellable last = lastCancellable();
        last.cancel();
        if (!lastRef.compareAndSet(last, Cancellables.of(onCancel))) {
            onCancel.cancel();
        }
    }

    @Override
    public void into(Cancellable cancellable) {
        cancellable.addCancellable(this);
    }

    @Override
    public boolean isCancelled() {
        return lastCancellable().isCancelled();
    }

    @Override
    public void cancel() {
        lastCancellable().cancel();
    }

    private Cancellable lastCancellable() {
        return lastRef.get();
    }
}
