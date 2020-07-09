package cn.thens.jack.flow;


import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;

/**
 * @author 7hens
 */
class FlowDelay<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;
    private final Func1<? super Reply<? extends T>, ? extends Flowable> delayFunc;

    private FlowDelay(Flow<T> upFlow, Func1<? super Reply<? extends T>, ? extends Flowable<?>> delayFunc) {
        this.upFlow = upFlow;
        this.delayFunc = delayFunc;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onStart(CollectorEmitter<? super T> emitter) {
        try {
            upFlow.collect(emitter, new Collector<T>() {
                @Override
                public void onCollect(Reply<? extends T> reply) {
                    try {
                        delayFunc.call(reply).asFlow()
                                .collect(emitter, new CollectorHelper() {
                                    @Override
                                    protected void onTerminate(Throwable error) throws Throwable {
                                        super.onTerminate(error);
                                        emitter.emit(reply);
                                    }
                                });
                    } catch (Throwable e) {
                        emitter.error(e);
                    }

                }
            });
        } catch (Throwable e) {
            emitter.error(e);
        }
    }

    public static <T> FlowDelay<T> delay(Flow<T> upFlow, Func1<? super Reply<? extends T>, ? extends Flowable<?>> delayFunc) {
        return new FlowDelay<>(upFlow, delayFunc);
    }

    public static <T> FlowDelay<T> delay(Flow<T> upFlow, Flowable<?> delayFlow) {
        return new FlowDelay<>(upFlow, Funcs.always(delayFlow));
    }
}
