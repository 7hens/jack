package cn.thens.jack.flow;


import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
class FlowTakeUntil<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Predicate<? super T> predicate;

    private FlowTakeUntil(Flow<T> upFlow, Predicate<? super T> predicate) {
        this.upFlow = upFlow;
        this.predicate = predicate;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.post(reply);
                    return;
                }
                try {
                    emitter.post(reply);
                    if (predicate.test(reply.next())) {
                        emitter.complete();
                    }
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        });
    }

    static <T> FlowTakeUntil<T> takeUntil(Flow<T> upFlow, Predicate<? super T> predicate) {
        return new FlowTakeUntil<T>(upFlow, predicate);
    }

    static <T> FlowTakeUntil<T> takeUntil(Flow<T> upFlow, T t) {
        return takeUntil(upFlow, Predicate.X.eq(t));
    }

    static <T> Flow<T> take(Flow<T> upFlow, int count) {
        if (count < 0) {
            return upFlow.takeLast(-count);
        }
        if (count == 0) {
            return Flow.empty();
        }
        return Flow.defer(() -> takeUntil(upFlow, Predicate.X.skip(count - 1)));
    }
}
