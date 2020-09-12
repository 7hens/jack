package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
public class OneOffJob implements Runnable, Cancellable {
    private Cancellable cancellable = Cancellables.create();
    private final Runnable runnable;

    public OneOffJob(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
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
    public void run() {
        if (!isCancelled()) {
            runnable.run();
            cancel();
        }
    }
}
