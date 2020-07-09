package cn.thens.jack.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class UnconfinedScheduler extends Scheduler {
    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (delay == 0) {
            runnable.run();
        } else {
            try {
                Thread.sleep(unit.toMillis(delay), (int) (unit.toNanos(delay) % 1000000));
                runnable.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return CompositeCancellable.cancelled();
    }
}
