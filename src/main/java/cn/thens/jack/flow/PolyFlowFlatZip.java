package cn.thens.jack.flow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 7hens
 */
class PolyFlowFlatZip<T> extends Flow<List<T>> {
    private final PolyFlow<T> upFlow;

    PolyFlowFlatZip(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super List<T>> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<IFlow<T>>() {
            final Queue<Queue<T>> cachedDataQueue = new LinkedList<>();
            final AtomicBoolean isOuterFlowTerminated = new AtomicBoolean(false);
            final PolyFlowFlatHelper helper = PolyFlowFlatHelper.create(emitter);

            @Override
            public void post(Reply<? extends IFlow<T>> reply) {
                if (reply.isComplete()) {
                    isOuterFlowTerminated.set(true);
                    tryZip();
                }
                helper.onOuterCollect(reply);
                if (emitter.isCancelled()) {
                    cachedDataQueue.clear();
                    return;
                }
                if (reply.isTerminal()) return;
                IFlow<T> flow = reply.next();
                Queue<T> dataQueue = new LinkedList<>();
                cachedDataQueue.add(dataQueue);
                try {
                    flow.asFlow().collectWith(emitter, newInnerCollector(dataQueue));
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }

            private Collector<T> newInnerCollector(Queue<T> dataQueue) {
                return new Collector<T>() {
                    @Override
                    public void post(Reply<? extends T> reply) {
                        helper.onInnerCollect(reply);
                        if (emitter.isCancelled()) {
                            cachedDataQueue.clear();
                            return;
                        }
                        if (reply.isTerminal()) return;
                        dataQueue.add(reply.next());
                        tryZip();
                    }
                };
            }

            private void tryZip() {
                if (!isOuterFlowTerminated.get()) return;
                if (cachedDataQueue.isEmpty()) return;
                List<T> result = new ArrayList<>();
                while (true) {
                    for (Queue<T> queue : cachedDataQueue) {
                        if (queue.isEmpty()) return;
                    }
                    for (Queue<T> queue : cachedDataQueue) {
                        result.add(queue.poll());
                    }
                    emitter.next(result);
                }
            }
        });
    }
}
