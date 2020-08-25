package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 7hens
 */
class FlowEmitterReplay<T> extends FlowEmitterPublish<T> {
    private List<Reply<? extends T>> replyBuffer = new CopyOnWriteArrayList<>();

    @Override
    protected void onCollect(Reply<? extends T> reply) throws Throwable {
        replyBuffer.add(reply);
        super.onCollect(reply);
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.create(emitter -> {
            for (Reply<? extends T> reply : replyBuffer) {
                emitter.post(reply);
            }
            super.asFlow().onStartCollect(emitter);
        });
    }
}
