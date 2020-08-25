package cn.thens.jack.flow;

/**
 * @author 7hens
 */
class FlowEmitterBehavior<T> extends FlowEmitterPublish<T> {
    private Reply<? extends T> lastReply;

    FlowEmitterBehavior(Reply<? extends T> initReply) {
        this.lastReply = initReply;
    }

    @Override
    protected void onCollect(Reply<? extends T> reply) throws Throwable {
        if (!reply.isTerminal()) {
            lastReply = reply;
        }
        super.onCollect(reply);
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.create(emitter -> {
            if (lastReply != null) {
                emitter.post(lastReply);
            }
            super.asFlow().onStartCollect(emitter);
        });
    }
}
