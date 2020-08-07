package cn.thens.jack.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.func.Lazy;

/**
 * @author 7hens
 */
public final class Schedulers {
    private Schedulers() {
    }

    private static Lazy<Scheduler> UNCONFINED = Lazy.of(() -> from(Runnable::run));

    public static Scheduler unconfined() {
        return UNCONFINED.get();
    }

    public static Scheduler from(final Executor executor) {
        if (executor instanceof ScheduledExecutorService) {
            return new ScheduledExecutorScheduler((ScheduledExecutorService) executor);
        }
        return new ExecutorScheduler(executor, timer());
    }

    private static Lazy<Scheduler> TIMER = Lazy.of(() ->
            from(Executors.newScheduledThreadPool(1, threadFactory("timer", true))));

    public static Scheduler timer() {
        return TIMER.get();
    }

    private static Lazy<Scheduler> IO = Lazy.of(() -> executorScheduler("io", 64));

    public static Scheduler io() {
        return IO.get();
    }

    private static Lazy<Scheduler> CORE = Lazy.of(() -> executorScheduler("core", 2));

    public static Scheduler core() {
        return CORE.get();
    }

    private static Scheduler executorScheduler(String name, int expectedThreadCount) {
        int processorCount = Runtime.getRuntime().availableProcessors();
        int maxThreadCount = Math.max(processorCount, expectedThreadCount);
        return from(new ThreadPoolExecutor(processorCount, maxThreadCount,
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
                threadFactory(name, false)));
    }

    private static ThreadFactory threadFactory(String name, boolean isSingle) {
        final AtomicLong n = new AtomicLong();
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(name + (isSingle ? "" : "-" + n.incrementAndGet()));
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.setDaemon(true);
            return thread;
        };
    }
}