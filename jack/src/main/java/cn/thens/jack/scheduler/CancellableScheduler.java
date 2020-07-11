package cn.thens.jack.scheduler;


import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
public class CancellableScheduler extends Scheduler implements Cancellable {
    private CompositeCancellable compositeCancellable = new CompositeCancellable();
    protected final Scheduler scheduler;

    CancellableScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        if (isCancelled()) return this;
        return addCancellable(scheduler.schedule(runnable));
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (isCancelled()) return this;
        return addCancellable(scheduler.schedule(runnable, delay, unit));
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        if (isCancelled()) return this;
        return addCancellable(scheduler.schedulePeriodically(runnable, initialDelay, period, unit));
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

    public CancellableScheduler flat() {
        return new FlatCancellableScheduler(scheduler);
    }

    private static class FlatCancellableScheduler extends CancellableScheduler {
        private FlatCancellableScheduler(Scheduler scheduler) {
            super(scheduler);
        }

        @Override
        public CancellableScheduler cancellable() {
            return new FlatCancellableScheduler(scheduler);
        }

        @Override
        public CancellableScheduler flat() {
            return this;
        }
    }
}
