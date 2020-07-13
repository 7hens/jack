package cn.thens.jack.scheduler;


import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class StructuredScheduler extends CancellableScheduler {
    private CompositeCancellable compositeCancellable = new CompositeCancellable();
    protected final Scheduler scheduler;

    StructuredScheduler(Scheduler scheduler) {
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

    @Override
    public CancellableScheduler cancellable() {
        return new StructuredScheduler(this);
    }

    @Override
    public CancellableScheduler flat() {
        return new FlatCancellableScheduler(scheduler);
    }

    private static class FlatCancellableScheduler extends StructuredScheduler {
        private FlatCancellableScheduler(Scheduler scheduler) {
            super(scheduler);
        }

        @Override
        public StructuredScheduler cancellable() {
            return new FlatCancellableScheduler(scheduler);
        }

        @Override
        public StructuredScheduler flat() {
            return this;
        }
    }
}
