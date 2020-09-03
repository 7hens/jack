package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class PolyFlowFlatMerge<T> extends Flow<T> {
    private final PolyFlow<T> upFlow;

    PolyFlowFlatMerge(PolyFlow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<IFlow<T>>() {
            final PolyFlowFlatHelper helper = PolyFlowFlatHelper.create(emitter);

            @Override
            public void post(Reply<? extends IFlow<T>> reply) {
                helper.onOuterCollect(reply);
                if (reply.isTerminal()) return;
                try {
                    reply.next().asFlow().collectWith(emitter, innerCollector);
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }

            private final Collector<T> innerCollector = new Collector<T>() {
                @Override
                public void post(Reply<? extends T> reply) {
                    helper.onInnerCollect(reply);
                    if (reply.isTerminal()) return;
                    emitter.post(reply);
                }
            };
        });
    }
}
