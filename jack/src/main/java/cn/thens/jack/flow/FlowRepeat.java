package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicInteger;

import cn.thens.jack.func.Func0;

/**
 * @author 7hens
 */
abstract class FlowRepeat<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;

    FlowRepeat(Flow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStart(Emitter<? super T> emitter) throws Throwable {
        upFlow.collect(emitter, new CollectorHelper<T>() {
            @Override
            protected void onEach(T data) throws Throwable {
                super.onEach(data);
                emitter.data(data);
            }

            @Override
            protected void onTerminate(Throwable error) throws Throwable {
                super.onTerminate(error);
                try {
                    onFlowTerminate(emitter);
                } catch (Throwable e) {
                    emitter.error(error);
                }
            }
        });
    }

    abstract void onFlowTerminate(Emitter<? super T> emitter) throws Throwable;

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow) {
        return new FlowRepeat<T>(upFlow) {
            @Override
            void onFlowTerminate(Emitter<? super T> emitter) throws Throwable {
                collect(emitter);
            }
        };
    }

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow, Func0<? extends Boolean> shouldRepeat) {
        return new FlowRepeat<T>(upFlow) {
            @Override
            void onFlowTerminate(Emitter<? super T> emitter) throws Throwable {
                if (shouldRepeat.call()) {
                    collect(emitter);
                }
            }
        };
    }

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow, int count) {
        return new FlowRepeat<T>(upFlow) {
            AtomicInteger resetCount = new AtomicInteger(count);

            @Override
            void onFlowTerminate(Emitter<? super T> emitter) throws Throwable {
                if (resetCount.decrementAndGet() >= 0) {
                    collect(emitter);
                }
            }
        };
    }
}
