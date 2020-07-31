package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicReference;

import cn.thens.jack.func.Func1;


/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public final class FlowX {
    private FlowX() {
    }

    public static <T> Func1<Flow<? extends IFlow<T>>, PolyFlow<T>> poly() {
        return flow -> new PolyFlow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super IFlow<T>> emitter) throws Throwable {
                flow.onStartCollect(emitter);
            }
        };
    }

    public static Func1<? super Flow<? extends IFlow<?>>, ? extends PolyFlow<Void>> terminalPoly() {
        return flows -> {
            return flows.map(it -> it.asFlow().skipAll().cast(Void.class))
                    .to(FlowX.poly());
        };
    }

    public static <Up, Dn> Operator<Up, Dn> pipe(Func1<? super Flow<Up>, ? extends IFlow<Dn>> action) {
        return new Operator<Up, Dn>() {
            @Override
            public Collector<? super Up> apply(Emitter<? super Dn> emitter) throws Throwable {
                AtomicReference<Emitter<? super Up>> upEmitterRef = new AtomicReference<>();
                Flow.<Up>create(upEmitterRef::set).to(action).asFlow().collectWith(emitter);
                return reply -> upEmitterRef.get().post(reply);
            }
        };
    }

    public static <Up, Dn> Operator<Up, Dn> wrap(FlowOperator<Up, Dn> operator) {
        return new Operator<Up, Dn>() {
            @Override
            public Collector<? super Up> apply(Emitter<? super Dn> emitter) throws Throwable {
                return operator.apply(emitter);
            }
        };
    }

    public static abstract class Operator<Up, Dn> implements FlowOperator<Up, Dn> {
        public <T> Operator<Up, T> then(FlowOperator<Dn, T> operator) {
            Operator<Up, Dn> self = this;
            return new Operator<Up, T>() {
                @Override
                public Collector<? super Up> apply(Emitter<? super T> emitter) throws Throwable {
                    return self.apply(CollectorEmitter.create(emitter, operator.apply(emitter)));
                }
            };
        }
    }
}
