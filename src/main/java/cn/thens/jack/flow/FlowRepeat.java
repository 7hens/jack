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
            protected void onNext(T data) throws Throwable {
                super.onNext(data);
                emitter.next(data);
            }

            @Override
            protected void onTerminate(Throwable error) throws Throwable {
                super.onTerminate(error);
                if (error == null) {
                    onFlowComplete(emitter);
                } else {
                    emitter.error(error);
                }
            }
        });
    }

    abstract void onFlowComplete(Emitter<? super T> emitter) throws Throwable;

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow) {
        return new FlowRepeat<T>(upFlow) {
            @Override
            void onFlowComplete(Emitter<? super T> emitter) throws Throwable {
                onStartCollect(emitter);
            }
        };
    }

    static <T> FlowRepeat<T> repeat(Flow<T> upFlow, Func0<? extends Boolean> shouldRepeat) {
        return new FlowRepeat<T>(upFlow) {
            @Override
            void onFlowComplete(Emitter<? super T> emitter) throws Throwable {
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
            void onFlowComplete(Emitter<? super T> emitter) throws Throwable {
                if (restCount.decrementAndGet() > 0) {
                    onStartCollect(emitter);
                } else {
                    emitter.complete();
                }
            }
        };
    }
}
