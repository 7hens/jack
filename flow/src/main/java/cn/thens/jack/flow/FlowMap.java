package cn.thens.jack.flow;


import cn.thens.jack.func.Func1;

/**
 * @author 7hens
 */
class FlowMap<T, R> extends Flow<R> {
    private final Flow<T> upFlow;
    private final Func1<? super T, ? extends R> mapper;

    FlowMap(Flow<T> upFlow, Func1<? super T, ? extends R> mapper) {
        this.upFlow = upFlow;
        this.mapper = mapper;
    }

    @Override
    protected void onStartCollect(Emitter<? super R> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.error(reply.error());
                    return;
                }
                try {
                    emitter.data(mapper.call(reply.data()));
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        });
    }
}
