package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class FlowWindow<T> extends PolyFlow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> windowFlow;
    private Emitter<? super T> currentEmitter;

    private FlowWindow(Flow<T> upFlow, IFlow<?> windowFlow) {
        this.upFlow = upFlow;
        this.windowFlow = windowFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super IFlow<T>> emitter) throws Throwable {
        emitNewFlow(emitter);
        windowFlow.asFlow().collectWith(emitter, reply -> {
            if (reply.isTerminal()) {
                emitter.cancel();
                emitReply(Reply.complete());
            } else {
                emitNewFlow(emitter);
            }
        });
        upFlow.collectWith(emitter, reply -> {
            emitReply(reply);
            if (reply.isTerminal()) {
                emitter.post(reply.newReply(null));
            }
        });
    }

    private void emitNewFlow(Emitter<? super IFlow<T>> emitter) {
        emitter.next(new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> innerEmitter) throws Throwable {
                emitReply(Reply.complete());
                currentEmitter = innerEmitter;
            }
        });
    }

    private void emitReply(Reply<? extends T> reply) {
        if (currentEmitter != null) {
            currentEmitter.post(reply);
        }
    }

    static <T> FlowWindow<T> window(Flow<T> upFlow, IFlow<?> windowFlowable) {
        return new FlowWindow<>(upFlow, windowFlowable);
    }
}
