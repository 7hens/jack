package cn.thens.jack.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 7hens
 */
class PolyFlowDelayErrors<T> extends AbstractPolyFlow<T> {
    private final PolyFlow<T> upFlow;

    PolyFlowDelayErrors(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStart(Emitter<? super IFlow<T>> emitter) throws Throwable {
        upFlow.collect(emitter, new Collector<IFlow<T>>() {
            final List<Throwable> errors = new ArrayList<>();
            final AtomicInteger restFlowCount = new AtomicInteger(1);

            @Override
            public void post(Reply<? extends IFlow<T>> reply) {
                if (reply.isTerminal()) {
                    Throwable error = reply.error();
                    if (error == null) {
                        onEachFlowTerminate();
                    } else {
                        emitter.error(error);
                    }
                    return;
                }
                restFlowCount.incrementAndGet();
                try {
                    emitter.data(reply.data().asFlow()
                            .onCollect(new CollectorHelper<T>() {
                                @Override
                                protected void onTerminate(Throwable error) throws Throwable {
                                    super.onTerminate(error);
                                    if (error != null) {
                                        errors.add(error);
                                    }
                                    onEachFlowTerminate();
                                }
                            })
                            .catchError(Flow.empty()));
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }

            private void onEachFlowTerminate() {
                if (restFlowCount.decrementAndGet() == 0) {
                    emitter.error(errors.isEmpty() ? null : new CompositeException(errors));
                }
            }
        });
    }
}
