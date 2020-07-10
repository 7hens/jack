package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class FlowWindow<T> extends AbstractPolyFlow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> windowFlowable;
    private Emitter<? super T> currentEmitter;

    private FlowWindow(Flow<T> upFlow, IFlow<?> windowFlowable) {
        this.upFlow = upFlow;
        this.windowFlowable = windowFlowable;
    }

    @Override
    protected void onStart(CollectorEmitter<? super IFlow<T>> emitter) throws Throwable {
        emitNewFlow(emitter);
        windowFlowable.asFlow().collect(emitter, reply -> {
            emitNewFlow(emitter);
            if (reply.isTerminal()) {
                emitReply(reply.newReply(null));
                emitter.error(reply.error());
            }
        });
        upFlow.collect(emitter, this::emitReply);
    }

    private void emitNewFlow(CollectorEmitter<? super IFlow<T>> emitter) {
        emitter.data(new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> innerEmitter) throws Throwable {
                emitReply(Reply.complete());
                currentEmitter = innerEmitter;
            }
        });
    }

    private void emitReply(Reply<? extends T> reply) {
        if (currentEmitter != null) {
            currentEmitter.emit(reply);
        }
    }

    static <T> FlowWindow<T> window(Flow<T> upFlow, IFlow<?> windowFlowable) {
        return new FlowWindow<>(upFlow, windowFlowable);
    }
}
