package cn.thens.jack.flow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 7hens
 */
class PolyFlowFlatJoin<T> extends Flow<List<T>> {
    private final PolyFlow<T> upFlow;

    PolyFlowFlatJoin(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super List<T>> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<IFlow<T>>() {
            final Queue<ValueRef<T>> cachedDataQueue = new LinkedList<>();
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
                IFlow<T> flow = reply.data();
                ValueRef<T> dataQueue = new ValueRef<>();
                cachedDataQueue.add(dataQueue);
                try {
                    flow.asFlow().collectWith(emitter, newInnerCollector(dataQueue));
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }

            private Collector<T> newInnerCollector(ValueRef<T> valueRef) {
                return new Collector<T>() {
                    @Override
                    public void post(Reply<? extends T> reply) {
                        helper.onInnerCollect(reply);
                        if (emitter.isCancelled()) {
                            cachedDataQueue.clear();
                            return;
                        }
                        if (reply.isTerminal()) return;
                        valueRef.set(reply.data());
                        tryZip();
                    }
                };
            }

            private void tryZip() {
                if (!isOuterFlowTerminated.get()) return;
                if (cachedDataQueue.isEmpty()) return;
                List<T> result = new ArrayList<>();
                for (ValueRef<T> valueRef : cachedDataQueue) {
                    if (!valueRef.hasValue()) return;
                }
                for (ValueRef<T> valueRef : cachedDataQueue) {
                    result.add(valueRef.get());
                }
                emitter.next(result);
            }
        });
    }

    private static class ValueRef<T> {
        private T value = null;
        private boolean isInitialized = false;

        public void set(T value) {
            isInitialized = true;
            this.value = value;
        }

        public T get() {
            return value;
        }

        public boolean hasValue() {
            return isInitialized;
        }
    }
}
