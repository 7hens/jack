package cn.thens.jack.flow;


import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
abstract class FlowCatch<T> extends Flow<T> {
    private final Flow<T> upFlow;

    private FlowCatch(Flow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isError()) {
                    try {
                        handleError(emitter, reply.error());
                    } catch (Throwable e) {
                        emitter.error(e);
                    }
                    return;
                }
                emitter.post(reply);
            }
        });
    }

    abstract void handleError(Emitter<? super T> emitter, Throwable error) throws Throwable;

    static <T> Flow<T> catchError(Flow<T> upFlow, Func1<? super Throwable, ? extends IFlow<T>> resumeFunc) {
        return new FlowCatch<T>(upFlow) {
            @Override
            void handleError(Emitter<? super T> emitter, Throwable error) throws Throwable {
                resumeFunc.call(error).asFlow().onStartCollect(emitter);
            }
        };
    }

    static <T> Flow<T> catchError(Flow<T> upFlow, Action2<? super Throwable, ? super Emitter<? super T>> resumeConsumer) {
        return new FlowCatch<T>(upFlow) {
            @Override
            void handleError(Emitter<? super T> emitter, Throwable error) throws Throwable {
                resumeConsumer.run(error, emitter);
            }
        };
    }

    static <T> Flow<T> catchError(Flow<T> upFlow, IFlow<T> resumeFlow) {
        return new FlowCatch<T>(upFlow) {
            @Override
            void handleError(Emitter<? super T> emitter, Throwable error) throws Throwable {
                resumeFlow.asFlow().onStartCollect(emitter);
            }
        };
    }

    static <T> Flow<T> retry(Flow<T> upFlow, Predicate<? super Throwable> predicate) {
        return new FlowCatch<T>(upFlow) {
            @Override
            void handleError(Emitter<? super T> emitter, Throwable error) throws Throwable {
                boolean shouldRetry = predicate.test(error);
                if (shouldRetry && !emitter.isCancelled()) {
                    onStartCollect(emitter);
                } else {
                    emitter.error(error);
                }
            }
        };
    }

    static <T> Flow<T> retry(Flow<T> upFlow) {
        return retry(upFlow, Predicate.X.alwaysTrue());
    }

    static <T> Flow<T> retry(Flow<T> upFlow, int count) {
        return Flow.defer(() -> retry(upFlow, Predicate.X.take(count)));
    }

    static <T> Flow<T> retry(Flow<T> upFlow, IFlow<?> timeoutFlow) {
        final Flow<T> fallbackFlow = Flow.error(new TimeoutException());
        return new FlowCatch<T>(upFlow) {
            private AtomicReference<Throwable> lastError = new AtomicReference<>();

            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                super.onStartCollect(emitter);
                timeoutFlow.asFlow().collectWith(emitter, reply -> {
                    if (reply.isTerminal() && !emitter.isCancelled()) {
                        Throwable error = lastError.get();
                        if (error != null) {
                            emitter.error(error);
                        } else {
                            fallbackFlow.onStartCollect(emitter);
                        }
                    }
                });
            }

            @Override
            void handleError(Emitter<? super T> emitter, Throwable error) throws Throwable {
                lastError.set(error);
                if (!emitter.isCancelled()) {
                    onStartCollect(emitter);
                }
            }
        };
    }
}
