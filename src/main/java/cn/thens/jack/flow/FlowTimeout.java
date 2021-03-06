package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Cancellables;

/**
 * @author 7hens
 */
class FlowTimeout<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> timeoutFlow;
    private final IFlow<T> fallback;
    private Cancellable upFlowCancellable = Cancellables.single();
    private Cancellable timeoutCancellable = Cancellables.single();
    private final AtomicBoolean isTransferred = new AtomicBoolean(false);

    FlowTimeout(Flow<T> upFlow, IFlow<?> timeoutFlow, IFlow<T> fallback) {
        this.upFlow = upFlow;
        this.timeoutFlow = timeoutFlow;
        this.fallback = fallback;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        isTransferred.set(false);
        timeoutCancellable.addCancellable(startTimeoutFlow(emitter));
        upFlowCancellable.addCancellable(upFlow.collectWith(emitter, reply -> {
            if (isTransferred.get()) return;
            emitter.post(reply);
            timeoutCancellable.cancel();
            if (reply.isTerminal() || emitter.isCancelled()) {
                return;
            }
            try {
                timeoutCancellable.addCancellable(startTimeoutFlow(emitter));
            } catch (Throwable e) {
                emitter.error(e);
            }
        }));
    }

    private Cancellable startTimeoutFlow(Emitter<? super T> emitter) throws Throwable {
        return timeoutFlow.asFlow().collectWith(emitter, reply -> {
            if (reply.isTerminal() && !reply.isCancel() && !emitter.isCancelled()) {
                if (isTransferred.compareAndSet(false, true)) {
                    try {
                        upFlowCancellable.cancel();
                        fallback.asFlow().onStartCollect(emitter);
                    } catch (Throwable e) {
                        emitter.error(e);
                    }
                }
            }
        });
    }
}
