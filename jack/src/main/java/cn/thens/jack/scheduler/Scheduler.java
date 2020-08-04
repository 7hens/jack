package cn.thens.jack.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
public abstract class Scheduler implements IScheduler {
    public abstract Cancellable schedule(Runnable runnable, long delay, TimeUnit unit);

    @Override
    public Cancellable schedule(Runnable runnable) {
        return schedule(runnable, 0L, TimeUnit.NANOSECONDS);
    }

    public Cancellable schedulePeriodically(final Runnable runnable, final long initialDelay,
                                            final long period, final TimeUnit unit) {
        final Cancellable cancellable = Cancellables.create();
        cancellable.addCancellable(schedule(new Runnable() {
            @Override
            public void run() {
                if (!cancellable.isCancelled()) {
                    runnable.run();
                    cancellable.addCancellable(schedule(this, period, unit));
                }
            }
        }, initialDelay, unit));
        return cancellable;
    }

    public IScheduler timer(long delay, TimeUnit unit) {
        return runnable -> schedule(runnable, delay, unit);
    }

    public IScheduler interval(long initialDelay, long period, TimeUnit unit) {
        return runnable -> schedulePeriodically(runnable, initialDelay, period, unit);
    }

    public IScheduler interval(long period, TimeUnit unit) {
        return interval(period, period, unit);
    }

    public CancellableScheduler cancellable() {
        return new CancellableScheduler(this);
    }
}
