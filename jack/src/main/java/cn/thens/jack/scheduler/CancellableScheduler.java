package cn.thens.jack.scheduler;


import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
public class CancellableScheduler extends Scheduler implements Cancellable {
    private Cancellable cancellable = Cancellables.create();
    private final Scheduler parent;

    CancellableScheduler(Scheduler parent) {
        this.parent = parent;
    }

    public Scheduler parent() {
        return parent;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        if (isCancelled()) return this;
        return cancellableOf(parent.schedule(runnable));
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (isCancelled()) return this;
        return cancellableOf(parent.schedule(runnable, delay, unit));
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        if (isCancelled()) return this;
        return cancellableOf(parent.schedulePeriodically(runnable, initialDelay, period, unit));
    }

    private Cancellable cancellableOf(Cancellable cancellable) {
        addCancellable(cancellable);
        return cancellable;
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        cancellable.addCancellable(onCancel);
    }

    @Override
    public void cancel() {
        cancellable.cancel();
    }

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
    }
}
