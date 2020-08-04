package cn.thens.jack.scheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class ScheduledExecutorScheduler extends Scheduler {
    private final ScheduledExecutorService executor;

    ScheduledExecutorScheduler(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        return Cancellables.from(executor.submit(runnable));
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        return Cancellables.from(executor.schedule(runnable, delay, unit));
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return Cancellables.from(executor.scheduleAtFixedRate(runnable, initialDelay, period, unit));
    }
}
