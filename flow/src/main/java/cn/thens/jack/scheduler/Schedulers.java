package cn.thens.jack.scheduler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
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
            from(Executors.newScheduledThreadPool(1, threadFactory("timer", 0, true))));

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
        int maxThreadCount = Math.max(processorCount + 1, expectedThreadCount);
        return from(new ThreadPoolExecutor(
                processorCount, processorCount,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
                threadFactory(name, 0, false),
                rejectHandler(processorCount, maxThreadCount, name)));
    }

    private static ThreadFactory threadFactory(String name, int startIndex, boolean isSingle) {
        final AtomicLong n = new AtomicLong(startIndex);
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(name + (isSingle ? "" : "-" + n.incrementAndGet()));
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.setDaemon(true);
            return thread;
        };
    }

    private static RejectedExecutionHandler rejectHandler(int coreCount, int maxCount, String name) {
        int threadCount = Math.max(1, maxCount - coreCount);
        final ThreadPoolExecutor fallbackExecutor = new ThreadPoolExecutor(
                threadCount, threadCount, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024), threadFactory(name, coreCount, threadCount == 1));
        fallbackExecutor.allowsCoreThreadTimeOut();
        return (runnable, threadPoolExecutor) -> fallbackExecutor.execute(runnable);
    }
}
