package cn.thens.jack.flow;


class FlowOnBackPressure<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final BackPressure<T> backPressure;

    FlowOnBackPressure(Flow<T> upFlow, BackPressure<T> backPressure) {
        this.upFlow = upFlow;
        this.backPressure = backPressure;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.onStartCollect(CollectorEmitter.create(emitter, emitter, backPressure));
    }
}
