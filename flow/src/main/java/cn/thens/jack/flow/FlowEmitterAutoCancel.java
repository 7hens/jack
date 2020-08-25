package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class FlowEmitterAutoCancel<T> extends FlowEmitter<T> {
    private final FlowEmitter<T> flowEmitter;

    FlowEmitterAutoCancel(FlowEmitter<T> flowEmitter) {
        this.flowEmitter = flowEmitter;
    }

    @Override
    protected Emitter<? super T> emitter() {
        return flowEmitter;
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.create(emitter -> {
            flowEmitter.asFlow().onStartCollect(emitter);
            emitter.addCancellable(() -> {
                if (!hasCollectors()) {
                    flowEmitter.cancel();
                }
            });
        });
    }

    @Override
    public boolean hasCollectors() {
        return flowEmitter.hasCollectors();
    }
}
