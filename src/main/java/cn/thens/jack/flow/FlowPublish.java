package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Funcs;

/**
 * @author 7hens
 */
class FlowPublish<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Func0<? extends FlowEmitter<T>> emitterFactory;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final AtomicReference<FlowEmitter<T>> emitterRef;

    FlowPublish(Flow<T> upFlow, Func0<? extends FlowEmitter<T>> emitterFactory) {
        this.upFlow = upFlow;
        this.emitterFactory = emitterFactory;
        this.emitterRef = new AtomicReference<>(Funcs.call(emitterFactory));
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        final FlowEmitter<T> flowEmitter = emitterRef.get();
        if (isStarted.compareAndSet(false, true)) {
            upFlow.onStartCollect(flowEmitter);
            flowEmitter.addCancellable(() -> {
                FlowEmitter<T> newEmitter = Funcs.call(emitterFactory);
                if (newEmitter != flowEmitter) {
                    isStarted.set(false);
                    emitterRef.set(newEmitter);
                }
            });
        }
        flowEmitter.asFlow().onStartCollect(emitter);
    }
}
