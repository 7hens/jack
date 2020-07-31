package cn.thens.jack.flow;


import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;

/**
 * @author 7hens
 */
class FlowDelay<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;
    private final Func1<? super Reply<? extends T>, ? extends IFlow<?>> delayFunc;

    private FlowDelay(Flow<T> upFlow, Func1<? super Reply<? extends T>, ? extends IFlow<?>> delayFunc) {
        this.upFlow = upFlow;
        this.delayFunc = delayFunc;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onStart(Emitter<? super T> emitter) throws Throwable {
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
    }

    public static <T> FlowDelay<T> delay(Flow<T> upFlow, Func1<? super Reply<? extends T>, ? extends IFlow<?>> delayFunc) {
        return new FlowDelay<>(upFlow, delayFunc);
    }

    public static <T> FlowDelay<T> delay(Flow<T> upFlow, IFlow<?> delayFlow) {
        return new FlowDelay<>(upFlow, Funcs.always(delayFlow));
    }
}
