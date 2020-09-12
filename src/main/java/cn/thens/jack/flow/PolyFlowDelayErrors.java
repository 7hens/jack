package cn.thens.jack.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 7hens
 */
class PolyFlowDelayErrors<T> extends PolyFlow<T> {
    private final PolyFlow<T> upFlow;

    PolyFlowDelayErrors(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super IFlow<T>> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<IFlow<T>>() {
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
                    emitter.next(reply.next().asFlow()
                            .onTerminate(error -> {
                                if (error != null) {
                                    errors.add(error);
                                }
                                onEachFlowTerminate();
                            })
                            .catchError());
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
