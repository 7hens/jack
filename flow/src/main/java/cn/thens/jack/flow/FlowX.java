package cn.thens.jack.flow;

import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;


/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public class FlowX {
    private FlowX() {
    }

    public static <T> Func1.X<Flow<? extends IFlow<T>>, PolyFlow<T>> poly() {
        return Funcs.of(flow -> new PolyFlow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super IFlow<T>> emitter) throws Throwable {
                flow.onStartCollect(emitter);
            }
        });
    }

    public static Func1.X<Flow<? extends IFlow<?>>, PolyFlow<Void>> terminalPoly() {
        return Funcs.of(flows -> {
            return flows.map(it -> it.asFlow().skipAll().cast(Void.class))
                    .to(FlowX.poly());
        });
    }
}
