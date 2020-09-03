package cn.thens.jack.flow;

import java.util.concurrent.atomic.AtomicInteger;

import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
abstract class FlowWindowFilter<T> extends PolyFlow<T> {
    private final Flow<T> upFlow;
    private Emitter<? super T> currentEmitter;

    private FlowWindowFilter(Flow<T> upFlow) {
        this.upFlow = upFlow;
    }

    abstract boolean shouldClose(T data) throws Throwable;

    @Override
    protected void onStartCollect(Emitter<? super IFlow<T>> emitter) throws Throwable {
        emitNewFlow(emitter);
        upFlow.collectWith(emitter, reply -> {
            if (reply.isTerminal()) {
                Throwable error = reply.error();
                if (currentEmitter != null) {
                    currentEmitter.error(error);
                } else {
                    emitter.next(Flow.error(error));
                }
                return;
            }
            try {
                emitInner(reply);
                if (shouldClose(reply.next())) {
                    emitNewFlow(emitter);
                }
            } catch (Throwable e) {
                emitInner(Reply.error(e));
            }
        });
    }

    private void emitNewFlow(Emitter<? super Flow<T>> emitter) {
        emitInner(Reply.complete());
        emitter.next(new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> innerEmitter) throws Throwable {
                currentEmitter = innerEmitter;
            }
        });
    }

    private void emitInner(Reply<? extends T> reply) {
        if (currentEmitter != null) {
            currentEmitter.post(reply);
        }
    }

    static <T> FlowWindowFilter<T> window(Flow<T> upFlow, Predicate<? super T> shouldClose) {
        return new FlowWindowFilter<T>(upFlow) {
            @Override
            boolean shouldClose(T data) throws Throwable {
                return shouldClose.test(data);
            }
        };
    }

    static <T> FlowWindowFilter<T> window(Flow<T> upFlow, final int count) {
        return new FlowWindowFilter<T>(upFlow) {
            AtomicInteger restCount = new AtomicInteger(count);

            @Override
            boolean shouldClose(T data) throws Throwable {
                if (restCount.decrementAndGet() == 0) {
                    restCount.set(count);
                    return true;
                }
                return false;
            }
        };
    }
}
