package cn.thens.jack.scheduler;

import java.util.concurrent.TimeUnit;

import cn.thens.jack.func.Exceptions;

/**
 * @author 7hens
 */
class UnconfinedScheduler extends Scheduler {
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
        try {
            Thread.sleep(unit.toMillis(delay), (int) (unit.toNanos(delay) % 1000000));
            runnable.run();
        } catch (Throwable e) {
            throw Exceptions.wrap(e);
        }
        return CompositeCancellable.cancelled();
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
