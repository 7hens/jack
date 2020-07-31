package cn.thens.jack.flow;


class FlowOnBackpressure<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Backpressure<T> backpressure;

    FlowOnBackpressure(Flow<T> upFlow, Backpressure<T> backpressure) {
        this.upFlow = upFlow;
        this.backpressure = backpressure;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.onStartCollect(CollectorEmitter.create(emitter, emitter, backpressure));
    }
}
