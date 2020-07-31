package cn.thens.jack.flow;

import java.util.concurrent.CancellationException;

import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
class FlowAutoSwitch<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> switchFlow;
    private final IFlow<T> fallback;

    private FlowAutoSwitch(Flow<T> upFlow, IFlow<?> switchFlow, IFlow<T> fallback) {
        this.upFlow = upFlow;
        this.switchFlow = switchFlow;
        this.fallback = fallback;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onStart(Emitter<? super T> emitter) throws Throwable {
        Cancellable cancellable = upFlow.collect(emitter, new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isCancel()) return;
                emitter.post(reply);
            }
        });
        switchFlow.asFlow().collect(emitter, new CollectorHelper() {
            @Override
            protected void onTerminate(Throwable error) throws Throwable {
                cancellable.cancel();
                fallback.asFlow().collect(emitter);
            }
        });
    }

    static <T> FlowAutoSwitch<T> autoSwitch(Flow<T> upFlow, IFlow<?> switchFlow, IFlow<T> fallback) {
        return new FlowAutoSwitch<>(upFlow, switchFlow, fallback);
    }

    static <T> FlowAutoSwitch<T> autoCancel(Flow<T> upFlow, IFlow<?> switchFlow) {
        return autoSwitch(upFlow, switchFlow, Flow.error(new CancellationException()));
    }
}
