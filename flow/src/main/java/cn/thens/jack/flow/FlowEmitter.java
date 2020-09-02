package cn.thens.jack.flow;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.ICancellable;


/**
 * @author 7hens
 */
public abstract class FlowEmitter<T> implements Emitter<T>, IFlow<T> {
    protected abstract Emitter<? super T> emitter();

    @Override
    public void post(Reply<? extends T> reply) {
        emitter().post(reply);
    }

    @Override
    public void next(T data) {
        post(Reply.next(data));
    }

    @Override
    public void error(Throwable error) {
        post(Reply.error(error));
    }

    @Override
    public void cancel() {
        post(Reply.cancel());
    }

    @Override
    public void complete() {
        post(Reply.complete());
    }

    @Override
    public boolean isCancelled() {
        return emitter().isCancelled();
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        emitter().addCancellable(onCancel);
    }

    @Override
    public void into(Cancellable cancellable) {
        cancellable.addCancellable(this);
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        return emitter().schedule(runnable);
    }

    @Override
    public abstract Flow<T> asFlow();

    public abstract boolean hasCollectors();

    public FlowEmitter<T> autoCancel() {
        return new FlowEmitterAutoCancel<>(this);
    }

    public static <T> FlowEmitter<T> publish() {
        return new FlowEmitterPublish<>();
    }

    public static <T> FlowEmitter<T> behavior() {
        return new FlowEmitterBehavior<>(null);
    }

    public static <T> FlowEmitter<T> behavior(T initData) {
        return new FlowEmitterBehavior<>(Reply.next(initData));
    }

    public static <T> FlowEmitter<T> replay() {
        return new FlowEmitterReplay<>();
    }
}
