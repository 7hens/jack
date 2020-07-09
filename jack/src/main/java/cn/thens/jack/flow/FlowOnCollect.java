package cn.thens.jack.flow;


import cn.thens.jack.func.Action0;
import cn.thens.jack.func.Action1;
import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
class FlowOnCollect<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;
    private final Collector<? super T> collector;

    FlowOnCollect(Flow<T> upFlow, Collector<? super T> collector) {
        this.upFlow = upFlow;
        this.collector = collector;
    }

    @Override
    protected void onStart(CollectorEmitter<? super T> emitter) throws Throwable {
        if (collector instanceof CollectorHelper) {
            try {
                ((CollectorHelper) collector).onStart(emitter);
            } catch (Throwable e) {
                emitter.error(e);
            }
        }
        upFlow.collect(emitter, new Collector<T>() {
            @Override
            public void onCollect(Reply<? extends T> reply) {
                try {
                    collector.onCollect(reply);
                } catch (Throwable e) {
                    emitter.error(e);
                }
                emitter.emit(reply);
            }
        });
    }

    static <T> FlowOnCollect<T> onCollect(Flow<T> upFlow, Collector<? super T> collector) {
        return new FlowOnCollect<>(upFlow, collector);
    }

    static <T> FlowOnCollect<T> onStart(Flow<T> upFlow, final Action1<? super Cancellable> consumer) {
        return onCollect(upFlow, new CollectorHelper<T>() {
            @Override
            protected void onStart(Cancellable cancellable) throws Throwable {
                super.onStart(cancellable);
                consumer.run(cancellable);
            }
        });
    }

    static <T> FlowOnCollect<T> onEach(Flow<T> upFlow, final Action1<? super T> consumer) {
        return onCollect(upFlow, new CollectorHelper<T>() {
            @Override
            protected void onEach(T data) throws Throwable {
                super.onEach(data);
                consumer.run(data);
            }
        });
    }

    static <T> FlowOnCollect<T> onTerminate(Flow<T> upFlow, final Action1<? super Throwable> consumer) {
        return onCollect(upFlow, new CollectorHelper<T>() {
            @Override
            protected void onTerminate(Throwable error) throws Throwable {
                super.onError(error);
                consumer.run(error);
            }
        });
    }

    static <T> FlowOnCollect<T> onComplete(Flow<T> upFlow, final Action0 action) {
        return onCollect(upFlow, new CollectorHelper<T>() {
            @Override
            protected void onTerminate(Throwable error) throws Throwable {
                super.onError(error);
                action.run();
            }
        });
    }

    static <T> FlowOnCollect<T> onError(Flow<T> upFlow, final Action1<? super Throwable> consumer) {
        return onCollect(upFlow, new CollectorHelper<T>() {
            @Override
            protected void onError(Throwable error) throws Throwable {
                super.onError(error);
                consumer.run(error);
            }
        });
    }

    static <T> FlowOnCollect<T> onCancel(Flow<T> upFlow, final Action0 action) {
        return onCollect(upFlow, new CollectorHelper<T>() {
            @Override
            protected void onCancel() throws Throwable {
                super.onCancel();
                action.run();
            }
        });
    }
}
