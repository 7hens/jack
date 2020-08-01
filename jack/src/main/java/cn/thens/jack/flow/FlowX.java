package cn.thens.jack.flow;

import cn.thens.jack.func.Func1;


/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public class FlowX {
    private FlowX() {
    }

    public static <T> Flow.Operator<T, Flow<T>> self() {
        return flow -> flow;
    }

    public static <T, F extends IFlow<T>>
    Flow.Operator<F, PolyFlow<T>>
    poly() {
        return flow -> new PolyFlow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super IFlow<T>> emitter) throws Throwable {
                flow.onStartCollect(emitter);
            }
        };
    }

    public static <F extends IFlow<?>>
    Flow.Operator<F, PolyFlow<Void>>
    terminalPoly() {
        return flows -> {
            return flows.map(it -> it.asFlow().skipAll().cast(Void.class))
                    .to(FlowX.poly());
        };
    }

    public static <T, R>
    Flow.Operator<T, Flow<R>>
    lift(Func1<? super Emitter<? super R>, ? extends Collector<? super T>> operator) {
        return flow -> {
            return Flow.create(emitter -> flow.collectWith(emitter, operator.call(emitter)));
        };
    }
}
