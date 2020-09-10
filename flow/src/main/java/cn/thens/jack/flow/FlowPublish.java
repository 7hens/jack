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
    private final AtomicReference<FlowEmitter<T>> flowEmitterRef;

    FlowPublish(Flow<T> upFlow, Func0<? extends FlowEmitter<T>> emitterFactory) {
        this.upFlow = upFlow;
        this.emitterFactory = emitterFactory;
        this.flowEmitterRef = new AtomicReference<>(Funcs.call(emitterFactory));
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        final FlowEmitter<T> flowEmitter = flowEmitterRef.get();
        if (isStarted.compareAndSet(false, true)) {
            Emitter<T> newEmitter = createEmitter(emitter, reply -> {
                flowEmitter.post(reply);
                if (reply.isCancel() || reply.isError()) {
                    FlowEmitter<T> newFlowEmitter = Funcs.call(emitterFactory);
                    if (newFlowEmitter != flowEmitter) {
                        isStarted.set(false);
                        flowEmitterRef.set(newFlowEmitter);
                    }
                }
            });
            upFlow.onStartCollect(newEmitter);
            flowEmitter.addCancellable(newEmitter);
        }
        flowEmitter.asFlow().onStartCollect(emitter);
    }
}
