package cn.thens.jack.scheduler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 7hens
 */
class CancellableImpl implements Cancellable {
    private final Set<ICancellable> cancelableSet = new CopyOnWriteArraySet<>();
    private final AtomicBoolean cancelFlag;

    CancellableImpl(boolean isCancelled) {
        cancelFlag = new AtomicBoolean(isCancelled);
    }

    @Override
    public void cancel() {
        if (cancelFlag.compareAndSet(false, true)) {
            onCancel();
            for (ICancellable cancellable : cancelableSet) {
                cancellable.cancel();
            }
            cancelableSet.clear();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelFlag.get();
    }

    protected void onCancel() {
    }

    public void addCancellable(ICancellable onCancel) {
        if (onCancel != this) {
            if (isCancelled()) {
                onCancel.cancel();
            } else {
                cancelableSet.add(onCancel);
            }
        }
    }
}
