package cn.thens.jack.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class ExecutorScheduler extends Scheduler {
    private static ThreadLocal<Executor> currentExecutor = new ThreadLocal<>();

    private final Scheduler scheduledHelper;
    private final Executor executor;

    ExecutorScheduler(Scheduler scheduledHelper, Executor executor) {
        this.scheduledHelper = scheduledHelper;
        this.executor = executor;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        if (currentExecutor.get() == executor) {
            runnable.run();
            return CompositeCancellable.cancelled();
        }
        CompositeCancellable cancellable = new CompositeCancellable();
        executor.execute(() -> {
//            currentExecutor.set(executor);
            if (!cancellable.isCancelled()) {
                runnable.run();
            }
        });
        return cancellable;
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        return scheduledHelper.schedule(executorRunnable(runnable), delay, unit);
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return scheduledHelper.schedulePeriodically(executorRunnable(runnable), initialDelay, period, unit);
    }

    private Runnable executorRunnable(final Runnable runnable) {
        return () -> executor.execute(runnable);
    }
}
