package cn.thens.jack.flow;

import java.util.Collection;

/**
 * @author 7hens
 */
class FlowToCollection<T, C extends Collection<T>> extends Flow<C> {
    private final Flow<T> upFlow;
    private final C list;

    FlowToCollection(Flow<T> upFlow, C collection) {
        this.upFlow = upFlow;
        this.list = collection;
    }

    @Override
    protected void onStartCollect(Emitter<? super C> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.next(list);
                    emitter.error(reply.error());
                    return;
                }
                list.add(reply.data());
            }
        });
    }
}
