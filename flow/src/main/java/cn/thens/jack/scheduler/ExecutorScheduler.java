package cn.thens.jack.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class ExecutorScheduler extends Scheduler {
    private final Executor executor;
    private final Scheduler timerScheduler;

    ExecutorScheduler(Executor executor, Scheduler timerScheduler) {
        this.executor = executor;
        this.timerScheduler = timerScheduler;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        OneOffJob job = new OneOffJob(runnable);
        executor.execute(job);
        return job;
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (delay == 0) {
            return schedule(runnable);
        }
        return timerScheduler.schedule(executorRunnable(runnable), delay, unit);
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return timerScheduler.schedulePeriodically(executorRunnable(runnable), initialDelay, period, unit);
    }

    private Runnable executorRunnable(final Runnable runnable) {
        return () -> executor.execute(runnable);
    }
}
