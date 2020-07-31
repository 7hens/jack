package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Cancellables;

/**
 * @author 7hens
 */
class FlowTimeout<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> timeoutFlow;
    private final IFlow<T> fallback;
    private Cancellable upFlowCancellable = Cancellables.create();
    private Cancellable timeoutCancellable = Cancellables.create();
    private final AtomicBoolean isTransferred = new AtomicBoolean(false);

    FlowTimeout(Flow<T> upFlow, IFlow<?> timeoutFlow, IFlow<T> fallback) {
        this.upFlow = upFlow;
        this.timeoutFlow = timeoutFlow;
        this.fallback = fallback;
    }

    @Override
    protected void onStart(Emitter<? super T> emitter) throws Throwable {
        timeoutCancellable = startTimeoutFlow(emitter);
        upFlowCancellable.addCancellable(upFlow.collect(emitter, reply -> {
            if (isTransferred.get()) return;
            emitter.post(reply);
            timeoutCancellable.cancel();
            if (reply.isTerminal() || emitter.isCancelled()) {
                return;
            }
            try {
                timeoutCancellable = startTimeoutFlow(emitter);
            } catch (Throwable e) {
                emitter.error(e);
            }
        }));
    }

    private Cancellable startTimeoutFlow(Emitter<? super T> emitter) throws Throwable {
        return timeoutFlow.asFlow().collect(emitter, reply -> {
            if (reply.isTerminal() && !emitter.isCancelled()) {
                upFlowCancellable.cancel();
                if (isTransferred.compareAndSet(false, true)) {
                    try {
                        fallback.asFlow().collect(emitter);
                    } catch (Throwable e) {
                        emitter.error(e);
                    }
                }
            }
        });
    }
}
