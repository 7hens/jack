package cn.thens.jack.flow;


import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
class FlowElementAt<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Predicate<? super T> predicate;

    private FlowElementAt(Flow<T> upFlow, Predicate<? super T> predicate) {
        this.upFlow = upFlow;
        this.predicate = predicate;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, reply -> {
            if (reply.isTerminal()) {
                emitter.post(reply);
                return;
            }
            try {
                if (predicate.test(reply.next())) {
                    emitter.post(reply);
                    emitter.complete();
                }
            } catch (Throwable e) {
                emitter.error(e);
            }
        });
    }

    static <T> FlowElementAt<T> first(Flow<T> upFlow, final Predicate<? super T> predicate) {
        return new FlowElementAt<T>(upFlow, predicate);
    }

    static <T> FlowElementAt<T> first(Flow<T> upFlow) {
        return first(upFlow, Predicate.X.alwaysTrue());
    }

    static <T> Flow<T> elementAt(final Flow<T> upFlow, final int index) {
        return defer(() -> first(upFlow, Predicate.X.skip(index)));
    }
}
