package cn.thens.jack.flow;


import java.util.concurrent.CancellationException;

import cn.thens.jack.func.Things;
import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
public abstract class CollectorHelper<T> implements Collector<T> {
    @Override
    public void post(Reply<? extends T> reply) {
        try {
            if (!reply.isTerminal()) {
                onEach(reply.data());
                return;
            }
            Throwable error = reply.error();
            onTerminate(error);
            if (error == null) {
                onComplete();
                return;
            }
            if (error instanceof CancellationException) {
                onCancel();
                return;
            }
            onError(error);
        } catch (Throwable e) {
            throw Things.wrap(e);
        }
    }

    protected void onStart(Cancellable cancellable) throws Throwable {
    }

    protected void onEach(T data) throws Throwable {
    }

    protected void onTerminate(Throwable error) throws Throwable {
    }

    protected void onComplete() throws Throwable {
    }

    protected void onError(Throwable error) throws Throwable {
    }

    protected void onCancel() throws Throwable {
    }

    @SuppressWarnings("unchecked")
    public static <T> CollectorHelper<T> from(final Collector<? super T> collector) {
        if (collector instanceof CollectorHelper) {
            return (CollectorHelper) collector;
        }
        return new CollectorHelper<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                super.post(reply);
                collector.post(reply);
            }
        };
    }

    private static CollectorHelper INSTANCE = new CollectorHelper() {
    };

    @SuppressWarnings("unchecked")
    public static <T> CollectorHelper<T> get() {
        return INSTANCE;
    }
}
