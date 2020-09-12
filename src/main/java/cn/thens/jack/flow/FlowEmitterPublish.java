package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import cn.thens.jack.scheduler.Schedulers;

/**
 * @author 7hens
 */
class FlowEmitterPublish<T> extends FlowEmitter<T> {
    private final AtomicReference<Reply<? extends T>> terminalReplyRef = new AtomicReference<>();
    private final List<Collector<? super T>> collectors = new CopyOnWriteArrayList<>();
    private final Emitter<T> emitter;

    FlowEmitterPublish() {
        Collector<T> collector = this::onCollect;
        emitter = CollectorEmitter.create(Schedulers.unconfined(), collector, BackPressures.success());
    }


    @Override
    protected Emitter<T> emitter() {
        return emitter;
    }

    @Override
    public void post(Reply<? extends T> reply) {
        if (!emitter.isCancelled() && reply.isTerminal()) {
            terminalReplyRef.compareAndSet(null, reply);
        }
        super.post(reply);
    }

    protected void onCollect(Reply<? extends T> reply) throws Throwable {
        for (Collector<? super T> collector : collectors) {
            collector.post(reply);
        }
        if (reply.isTerminal()) {
            collectors.clear();
        }
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.create(emitter -> {
            Reply<? extends T> terminalReply = terminalReplyRef.get();
            if (terminalReply != null) {
                emitter.post(terminalReply);
                return;
            }
            collectors.add(emitter);
            emitter.addCancellable(() -> collectors.remove(emitter));
        });
    }

    @Override
    public boolean hasCollectors() {
        return !collectors.isEmpty();
    }
}
