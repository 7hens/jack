package cn.thens.jack.flow;

import cn.thens.jack.func.Func1;

/**
 * @author 7hens
 */
class FlowLift<T, R> extends Flow<R> {
    private final Flow<T> upFlow;
    private final Func1<? super Emitter<? super R>, ? extends Collector<? super T>> operator;

    private FlowLift(Flow<T> upFlow,
                     Func1<? super Emitter<? super R>, ? extends Collector<? super T>> operator) {
        this.upFlow = upFlow;
        this.operator = operator;
    }

    @Override
    protected void onStartCollect(Emitter<? super R> emitter) throws Throwable {
        Collector<? super T> collector = operator.call(emitter);
        if (collector instanceof CollectorHelper) {
            ((CollectorHelper) collector).onStart(emitter);
        }
        upFlow.collectWith(emitter, collector);
    }

    static <T, R> Flow<R> lift(
            Flow<T> upFlow,
            Func1<? super Emitter<? super R>, ? extends Collector<? super T>> operator) {
        return new FlowLift<>(upFlow, operator);
    }

    static <T, R> Flow<R> skipAllTo(Flow<T> upFlow, IFlow<R> next) {
        return lift(upFlow, emitter -> {
            return new CollectorHelper<T>() {
                @Override
                protected void onComplete() throws Throwable {
                    super.onComplete();
                    next.asFlow().onStartCollect(emitter);
                }
            };
        });
    }
}
