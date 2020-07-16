package cn.thens.jack.scheduler;


import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
public class CancellableScheduler extends Scheduler implements Cancellable {
    private CompositeCancellable compositeCancellable = new CompositeCancellable();
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
        return addCancellable(parent.schedule(runnable));
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (isCancelled()) return this;
        return addCancellable(parent.schedule(runnable, delay, unit));
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        if (isCancelled()) return this;
        return addCancellable(parent.schedulePeriodically(runnable, initialDelay, period, unit));
    }

    private Cancellable addCancellable(Cancellable cancellable) {
        compositeCancellable.addCancellable(cancellable);
        return cancellable;
    }

    @Override
    public void cancel() {
        compositeCancellable.cancel();
    }

    @Override
    public boolean isCancelled() {
        return compositeCancellable.isCancelled();
    }
}
