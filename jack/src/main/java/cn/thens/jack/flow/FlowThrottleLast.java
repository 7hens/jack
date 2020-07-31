package cn.thens.jack.flow;


import java.util.concurrent.CancellationException;

import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Cancellables;

/**
 * @author 7hens
 */
class FlowThrottleLast<T> implements FlowOperator<T, T> {
    private final Func1<? super T, ? extends IFlow<?>> flowFactory;
    private Cancellable lastFlow = Cancellables.cancelled();

    private FlowThrottleLast(Func1<? super T, ? extends IFlow<?>> flowFactory) {
        this.flowFactory = flowFactory;
    }

    @Override
    public Collector<T> apply(Emitter<? super T> emitter) {
        return new Collector<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.post(reply);
                    return;
                }
                try {
                    lastFlow.cancel();
                    lastFlow = flowFactory.call(reply.data())
                            .asFlow()
                            .collectWith(emitter, new CollectorHelper() {
                                @Override
                                protected void onTerminate(Throwable error) throws Throwable {
                                    super.onTerminate(error);
                                    if (!(error instanceof CancellationException)) {
                                        emitter.post(reply);
                                    }
                                }
                            });
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        };
    }

    static <T> FlowOperator<T, T> throttleLast(Func1<? super T, ? extends IFlow<?>> flowFactory) {
        return new FlowThrottleLast<T>(flowFactory);
    }

    static <T> FlowOperator<T, T> throttleLast(IFlow<?> flow) {
        return new FlowThrottleLast<T>(Funcs.always(flow));
    }
}
