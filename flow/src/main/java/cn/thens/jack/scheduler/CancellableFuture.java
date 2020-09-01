package cn.thens.jack.scheduler;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 7hens
 */
final class CancellableFuture<V> implements Future<V>, Cancellable {
    private final Future<V> future;
    private final Cancellable cancellable;

    CancellableFuture(Future<V> future) {
        this.future = future;
        this.cancellable = new CancellableImpl(future.isCancelled() || future.isDone());
    }

    @Override
    public boolean cancel(boolean b) {
        cancellable.cancel();
        return future.cancel(b);
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public V get(long timeout, @NotNull TimeUnit timeUnit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, timeUnit);
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
    public void into(Cancellable cancellable) {
        cancellable.addCancellable(this);
    }

    @Override
    public void cancel() {
        cancel(false);
    }
}
