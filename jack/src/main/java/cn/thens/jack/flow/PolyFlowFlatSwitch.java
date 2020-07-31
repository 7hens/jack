package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicReference;

import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
class PolyFlowFlatSwitch<T> extends AbstractFlow<T> {
    private final PolyFlow<T> upFlow;

    PolyFlowFlatSwitch(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStart(Emitter<? super T> emitter) throws Throwable {
        upFlow.collect(emitter, new Collector<IFlow<T>>() {
            final AtomicReference<Cancellable> lastCancellable = new AtomicReference<>(null);
            final PolyFlowFlatHelper helper = PolyFlowFlatHelper.create(emitter);

            @Override
            public void post(Reply<? extends IFlow<T>> reply) {
                helper.onOuterCollect(reply);
                if (reply.isTerminal()) return;
                Cancellable cancellable = lastCancellable.get();
                if (cancellable != null) {
                    cancellable.cancel();
                }
                IFlow<T> flow = reply.data();
                try {
                    lastCancellable.set(flow.asFlow().collect(emitter, innerCollector));
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }

            private final Collector<T> innerCollector = new Collector<T>() {
                @Override
                public void post(Reply<? extends T> reply) {
                    if (reply.isCancel()) {
                        helper.onInnerCollect(Reply.complete());
                        return;
                    }
                    helper.onInnerCollect(reply);
                    if (reply.isTerminal()) return;
                    emitter.post(reply);
                }
            };
        });
    }
}
