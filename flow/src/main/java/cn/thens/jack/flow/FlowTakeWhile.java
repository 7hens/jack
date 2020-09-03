package cn.thens.jack.flow;


import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
class FlowTakeWhile<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Predicate<? super T> predicate;

    private FlowTakeWhile(Flow<T> upFlow, Predicate<? super T> predicate) {
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
                    if (predicate.test(reply.next())) {
                        emitter.post(reply);
                    } else {
                        emitter.complete();
                    }
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        });
    }

    static <T> Flow<T> takeWhile(Flow<T> upFlow, Predicate<? super T> predicate) {
        return new FlowTakeWhile<>(upFlow, predicate);
    }

    static <T> Flow<T> take(Flow<T> upFlow, int count) {
        if (count < 0) {
            return upFlow.takeLast(-count);
        }
        if (count == 0) {
            return Flow.empty();
        }
        return Flow.defer(() -> takeWhile(upFlow, Predicate.X.take(count)));
    }
}
