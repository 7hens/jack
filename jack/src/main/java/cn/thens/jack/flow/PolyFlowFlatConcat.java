package cn.thens.jack.flow;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 7hens
 */
class PolyFlowFlatConcat<T> extends AbstractFlow<T> {
    private final PolyFlow<T> upFlow;

    PolyFlowFlatConcat(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStart(Emitter<? super T> emitter) throws Throwable {
        upFlow.collect(emitter, new Collector<IFlow<T>>() {
            final Queue<IFlow<T>> flowQueue = new LinkedList<>();
            final AtomicBoolean isCollecting = new AtomicBoolean(false);
            final PolyFlowFlatHelper helper = PolyFlowFlatHelper.create(emitter);

            @Override
            public void post(Reply<? extends IFlow<T>> reply) {
                helper.onOuterCollect(reply);
                if (reply.isTerminal()) return;
                IFlow<T> flowable = reply.data();
                if (isCollecting.compareAndSet(false, true)) {
                    try {
                        flowable.asFlow().collect(emitter, innerCollector);
                    } catch (Throwable e) {
                        emitter.error(e);
                    }
                    return;
                }
                flowQueue.add(flowable);
            }

            private final Collector<T> innerCollector = new Collector<T>() {
                @Override
                public void post(Reply<? extends T> reply) {
                    helper.onInnerCollect(reply);
                    if (emitter.isCancelled()) {
                        flowQueue.clear();
                        return;
                    }
                    isCollecting.set(true);
                    if (reply.isTerminal()) {
                        if (!flowQueue.isEmpty()) {
                            try {
                                flowQueue.poll().asFlow().collect(emitter, this);
                            } catch (Throwable e) {
                                emitter.error(e);
                            }
                        } else {
                            isCollecting.set(false);
                        }
                        return;
                    }
                    emitter.post(reply);
                }
            };
        });
    }
}
