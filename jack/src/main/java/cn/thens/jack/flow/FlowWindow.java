package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class FlowWindow<T> extends AbstractPolyFlow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> windowFlow;
    private Emitter<? super T> currentEmitter;

    private FlowWindow(Flow<T> upFlow, IFlow<?> windowFlow) {
        this.upFlow = upFlow;
        this.windowFlow = windowFlow;
    }

    @Override
    protected void onStart(Emitter<? super IFlow<T>> emitter) throws Throwable {
        emitNewFlow(emitter);
        windowFlow.asFlow().collect(emitter, reply -> {
            if (reply.isTerminal()) {
                emitter.cancel();
                emitReply(Reply.complete());
            } else {
                emitNewFlow(emitter);
            }
        });
        upFlow.collect(emitter, reply -> {
            emitReply(reply);
            if (reply.isTerminal()) {
                emitter.post(reply.newReply(null));
            }
        });
    }

    private void emitNewFlow(Emitter<? super IFlow<T>> emitter) {
        emitter.data(new AbstractFlow<T>() {
            @Override
            protected void onStart(Emitter<? super T> innerEmitter) throws Throwable {
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
