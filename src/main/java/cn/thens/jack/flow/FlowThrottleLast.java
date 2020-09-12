package cn.thens.jack.flow;


import java.util.concurrent.CancellationException;

import cn.thens.jack.func.Func1;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Cancellables;

/**
 * @author 7hens
 */
class FlowThrottleLast<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Func1<? super T, ? extends IFlow<?>> flowFactory;
    private Cancellable lastFlow = Cancellables.cancelled();

    FlowThrottleLast(Flow<T> upFlow, Func1<? super T, ? extends IFlow<?>> flowFactory) {
        this.upFlow = upFlow;
        this.flowFactory = flowFactory;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.post(reply);
                    return;
                }
                try {
                    lastFlow.cancel();
                    lastFlow = flowFactory.call(reply.data()).asFlow()
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
        });
    }
}
