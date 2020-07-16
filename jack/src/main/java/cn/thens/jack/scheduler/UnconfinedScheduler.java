package cn.thens.jack.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

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
        Thread thread = Thread.currentThread();
        CompositeCancellable cancellable = new CompositeCancellable() {
            @Override
            protected void onCancel() {
                LockSupport.unpark(thread);
            }
        };
        cancellable.addCancellable(scheduledHelper.schedule(() -> {
            LockSupport.unpark(thread);
        }, delay, unit));
        LockSupport.park();
        if (!cancellable.isCancelled()) {
            runnable.run();
        }
        return cancellable;
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
