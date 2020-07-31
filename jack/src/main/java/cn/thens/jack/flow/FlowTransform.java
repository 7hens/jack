package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class FlowTransform<T, R> extends Flow<R> {
    private final Flow<T> upFlow;
    private final FlowOperator<? super T, ? extends R> operator;

    FlowTransform(Flow<T> upFlow, FlowOperator<? super T, ? extends R> operator) {
        this.upFlow = upFlow;
        this.operator = operator;
    }

    @Override
    protected void onStartCollect(Emitter<? super R> emitter) throws Throwable {
        upFlow.onStartCollect(CollectorEmitter.create(emitter, operator.apply(emitter)));
    }
}
