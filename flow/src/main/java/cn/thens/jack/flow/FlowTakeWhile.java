package cn.thens.jack.flow;


import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
class FlowTakeWhile<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Predicate<? super T> predicate;

    FlowTakeWhile(Flow<T> upFlow, Predicate<? super T> predicate) {
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
                    if (predicate.test(reply.data())) {
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
}
