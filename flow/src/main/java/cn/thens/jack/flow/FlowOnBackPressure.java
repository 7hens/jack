package cn.thens.jack.flow;


class FlowOnBackPressure<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final BackPressure<T> backpressure;

    FlowOnBackPressure(Flow<T> upFlow, BackPressure<T> backpressure) {
        this.upFlow = upFlow;
        this.backpressure = backpressure;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.onStartCollect(CollectorEmitter.create(emitter, emitter, backpressure));
    }
}
