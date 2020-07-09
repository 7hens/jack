package cn.thens.jack.flow;


import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.CompositeCancellable;

/**
 * @author 7hens
 */
class FlowThrottleFirst<T> implements FlowOperator<T, T> {
    private final Func1<? super T, ? extends Flowable<?>> flowFactory;
    private final AtomicBoolean couldEmit = new AtomicBoolean(true);
    private Cancellable lastFlow = CompositeCancellable.cancelled();

    private FlowThrottleFirst(Func1<? super T, ? extends Flowable<?>> flowFactory) {
        this.flowFactory = flowFactory;
    }

    @Override
    public Collector<T> apply(Emitter<? super T> emitter) {
        return new Collector<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onCollect(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.emit(reply);
                    return;
                }
                try {
                    lastFlow.cancel();
                    lastFlow = flowFactory.invoke(reply.data()).asFlow()
                            .collect(emitter, new CollectorHelper() {
                                @Override
                                protected void onTerminate(Throwable error) throws Throwable {
                                    super.onTerminate(error);
                                    if (!(error instanceof CancellationException)) {
                                        couldEmit.set(true);
                                    }
                                }
                            });
                    if (couldEmit.compareAndSet(true, false)) {
                        emitter.emit(reply);
                    }
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        };
    }

    static <T> FlowOperator<T, T> throttleFirst(Func1<? super T, ? extends Flowable<?>> flowFactory) {
        return new FlowThrottleFirst<>(flowFactory);
    }

    static <T> FlowOperator<T, T> throttleFirst(Flowable<?> flow) {
        return new FlowThrottleFirst<>(Funcs.always(flow));
    }
}
