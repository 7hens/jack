package cn.thens.jack.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.thens.jack.ref.Ref;

/**
 * @author 7hens
 */
public final class Schedulers {
    private Schedulers() {
    }

    private static Scheduler UNCONFINED = new UnconfinedScheduler();

    public static Scheduler unconfined() {
        return UNCONFINED;
    }

    public static Scheduler from(final Executor executor) {
        if (executor instanceof ScheduledExecutorService) {
            return new ScheduledExecutorScheduler((ScheduledExecutorService) executor);
        }
        return new ExecutorScheduler(single(), executor);
    }

    private static Ref<Scheduler> SINGLE = Ref.lazy(() -> {
        return from(Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("single");
            thread.setDaemon(true);
            return thread;
        }));
    }).lazy();

    public static Scheduler single() {
        return SINGLE.get();
    }

    private static Ref<Scheduler> IO = Ref.lazy(() -> newScheduler("io", 64));

    public static Scheduler io() {
        return IO.get();
    }

    private static Ref<Scheduler> CORE = Ref.lazy(() -> newScheduler("core", 2));

    public static Scheduler core() {
        return CORE.get();
    }

    private static Scheduler newScheduler(String name, int expectedThreadCount) {
        int processorCount = Runtime.getRuntime().availableProcessors();
        int maxThreadCount = Math.max(processorCount, expectedThreadCount);
        return from(new ThreadPoolExecutor(processorCount, maxThreadCount,
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
                new SchedulerThreadFactory(name)));
    }
}
