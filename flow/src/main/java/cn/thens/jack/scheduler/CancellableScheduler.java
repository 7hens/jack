package cn.thens.jack.scheduler;


import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
public class CancellableScheduler extends Scheduler implements Cancellable {
    private Cancellable cancellable = Cancellables.create();
    private final Scheduler parent;

    CancellableScheduler(Scheduler parent) {
        this.parent = parent;
    }

    public Scheduler parent() {
        return parent;
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        if (isCancelled()) return this;
        Cancellable job = Cancellables.create();
        Runnable newRunnable = wrapRunnable(runnable, job);
        job.addCancellable(parent.schedule(newRunnable));
        return job;
    }

    @Override
    public Cancellable schedule(Runnable runnable, long delay, TimeUnit unit) {
        if (isCancelled()) return this;
        Cancellable job = Cancellables.create();
        Runnable newRunnable = wrapRunnable(runnable, job);
        job.addCancellable(parent.schedule(newRunnable, delay, unit));
        return job;
    }

    @Override
    public Cancellable schedulePeriodically(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        if (isCancelled()) return this;
        Cancellable job = Cancellables.create();
        Runnable newRunnable = wrapRunnable(runnable, job);
        job.addCancellable(parent.schedulePeriodically(newRunnable, initialDelay, period, unit));
        return job;
    }

    private Runnable wrapRunnable(Runnable runnable, Cancellable cancellable) {
        addCancellable(cancellable);
        return () -> {
            if (!cancellable.isCancelled()) {
                runnable.run();
                cancellable.cancel();
            }
        };
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        cancellable.addCancellable(onCancel);
    }


    @Override
    public void cancel() {
        cancellable.cancel();
    }

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
    }
}
