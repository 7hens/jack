package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.ICancellable;
import cn.thens.jack.scheduler.Scheduler;
import cn.thens.jack.scheduler.Schedulers;


/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public class FlowEmitter<T> implements Emitter<T>, IFlow<T> {
    private final AtomicReference<Reply<? extends T>> terminalReplyRef = new AtomicReference<>();
    private final List<Emitter<? super T>> emitters = new CopyOnWriteArrayList<>();
    private final CollectorEmitter<T> collectorEmitter;

    private FlowEmitter() {
        Scheduler scheduler = Schedulers.unconfined();
        Collector<T> collector = this::onCollect;
        collectorEmitter = CollectorEmitter.create(scheduler, collector);
    }

    @Override
    public void post(Reply<? extends T> reply) {
        if (reply.isTerminal()) {
            terminalReplyRef.compareAndSet(null, reply);
        }
        collectorEmitter.post(reply);
    }

    @Override
    public void data(T data) {
        post(Reply.data(data));
    }

    @Override
    public void error(Throwable error) {
        post(Reply.error(error));
    }

    @Override
    public void cancel() {
        post(Reply.cancel());
    }

    @Override
    public void complete() {
        post(Reply.complete());
    }

    @Override
    public boolean isCancelled() {
        return collectorEmitter.isCancelled();
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        collectorEmitter.addCancellable(onCancel);
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        return collectorEmitter.schedule(runnable);
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.create(emitter -> {
            Reply<? extends T> terminalReply = terminalReplyRef.get();
            if (terminalReply != null) {
                emitter.post(terminalReply);
                return;
            }
            emitters.add(emitter);
            emitter.addCancellable(() -> emitters.remove(emitter));
        });
    }

    protected void onCollect(Reply<? extends T> reply) {
        for (Emitter<? super T> emitter : emitters) {
            emitter.post(reply);
        }
        if (reply.isTerminal()) {
            emitters.clear();
        }
    }

    public static <T> FlowEmitter<T> publish() {
        return new FlowEmitter<>();
    }

    private static <T> FlowEmitter<T> behavior(Reply<? extends T> initReply) {
        return new FlowEmitter<T>() {
            private Reply<? extends T> lastReply = initReply;

            @Override
            protected void onCollect(Reply<? extends T> reply) {
                if (!reply.isTerminal()) {
                    lastReply = reply;
                }
                super.onCollect(reply);
            }

            @Override
            public Flow<T> asFlow() {
                return Flow.<T>create(emitter -> {
                    if (lastReply != null) {
                        emitter.post(lastReply);
                    }
                    emitter.complete();
                }) ////////////////
                        .polyWith(super.asFlow())
                        .flatMerge();
            }
        };
    }

    public static <T> FlowEmitter<T> behavior() {
        return behavior(null);
    }

    public static <T> FlowEmitter<T> behavior(T initData) {
        return behavior(Reply.data(initData));
    }

    public static <T> FlowEmitter<T> replay() {
        return new FlowEmitter<T>() {
            private List<Reply<? extends T>> replyBuffer = new CopyOnWriteArrayList<>();

            @Override
            protected void onCollect(Reply<? extends T> reply) {
                replyBuffer.add(reply);
                super.onCollect(reply);
            }

            @Override
            public Flow<T> asFlow() {
                return Flow.<T>create(emitter -> {
                    for (Reply<? extends T> reply : replyBuffer) {
                        emitter.post(reply);
                    }
                    emitter.complete();
                }).polyWith(super.asFlow()).flatConcat();
            }
        };
    }
}
