package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicInteger;

import cn.thens.jack.func.Func0;

/**
 * @author 7hens
 */
abstract class FlowRepeat<T> extends Flow<T> {
    private final Flow<T> upFlow;

    FlowRepeat(Flow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, new CollectorHelper<T>() {
            @Override
            protected void onEach(T data) throws Throwable {
                super.onEach(data);
                emitter.data(data);
            }

            @Override
            protected void onComplete() throws Throwable {
                super.onComplete();
                try {
                    onFlowTerminate(emitter);
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        });
    }

    abstract void onFlowTerminate(Emitter<? super T> emitter) throws Throwable;

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow) {
        return new FlowRepeat<T>(upFlow) {
            @Override
            void onFlowTerminate(Emitter<? super T> emitter) throws Throwable {
                onStartCollect(emitter);
            }
        };
    }

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow, Func0<? extends Boolean> shouldRepeat) {
        return new FlowRepeat<T>(upFlow) {
            @Override
            void onFlowTerminate(Emitter<? super T> emitter) throws Throwable {
                if (shouldRepeat.call()) {
                    onStartCollect(emitter);
                } else {
                    emitter.complete();
                }
            }
        };
    }

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow, int count) {
        return new FlowRepeat<T>(upFlow) {
            AtomicInteger restCount = new AtomicInteger(count);

            @Override
            void onFlowTerminate(Emitter<? super T> emitter) throws Throwable {
                if (restCount.decrementAndGet() > 0) {
                    onStartCollect(emitter);
                } else {
                    emitter.complete();
                }
            }
        };
    }
}
