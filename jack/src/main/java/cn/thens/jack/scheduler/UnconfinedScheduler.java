package cn.thens.jack.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class UnconfinedScheduler extends Scheduler {
    private final Scheduler scheduledHelper;

    UnconfinedScheduler(Scheduler scheduledHelper) {
        this.scheduledHelper = scheduledHelper;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        runnable.run();
        return CompositeCancellable.cancelled();
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (delay == 0) {
            return schedule(runnable);
        }
        return scheduledHelper.schedule(runnable, delay, unit);
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return scheduledHelper.schedulePeriodically(runnable, initialDelay, period, unit);
    }

    @Override
    public CancellableScheduler cancellable() {
        return new CancellableScheduler(this) {
            @Override
            public CancellableScheduler cancellable() {
                return this;
            }
        };
    }
}
