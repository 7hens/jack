package cn.thens.jack.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.CompositeCancellable;
import cn.thens.jack.scheduler.Scheduler;
import cn.thens.jack.scheduler.Schedulers;


/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public class FlowEmitter<T> implements Emitter<T>, IFlow<T> {
    private Reply<? extends T> terminalReply = null;
    private final List<Emitter<? super T>> emitters = new CopyOnWriteArrayList<>();
    private final CollectorEmitter<T> collectorEmitter =
            CollectorEmitter.create(Schedulers.core(), reply -> {
                FlowEmitter.this.onCollect(reply);
                for (Emitter<? super T> emitter : emitters) {
                    emitter.emit(reply);
                }
                if (reply.isTerminal()) {
                    emitters.clear();
                }
            });

    private FlowEmitter() {
    }

    @Override
    public void emit(Reply<? extends T> reply) {
        if (reply.isTerminal()) {
            terminalReply = reply;
        }
        collectorEmitter.emit(reply);
    }

    @Override
    public void data(T data) {
        emit(Reply.data(data));
    }

    @Override
    public void error(Throwable error) {
        emit(Reply.error(error));
    }

    @Override
    public void cancel() {
        error(new CancellationException());
    }

    @Override
    public void complete() {
        emit(Reply.complete());
    }

    @Override
    public boolean isTerminated() {
        return collectorEmitter.isTerminated();
    }

    @Override
    public void addCancellable(Cancellable cancellable) {
        collectorEmitter.addCancellable(cancellable);
    }

    @Override
    public Scheduler scheduler() {
        return collectorEmitter.scheduler();
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.create(emitter -> {
            if (terminalReply != null) {
                emitter.emit(terminalReply);
                return;
            }
            emitters.add(emitter);
            emitter.addCancellable(new CompositeCancellable() {
                @Override
                protected void onCancel() {
                    super.onCancel();
                    emitters.remove(emitter);
                }
            });
        });
    }

    protected void onCollect(Reply<? extends T> reply) {
    }

    public static <T> FlowEmitter<T> publish() {
        return new FlowEmitter<>();
    }

    private static <T> FlowEmitter<T> behavior(Reply<? extends T> initReply) {
        return new FlowEmitter<T>() {
            private Reply<? extends T> lastReply = initReply;

            @Override
            protected void onCollect(Reply<? extends T> reply) {
                super.onCollect(reply);
                if (!reply.isTerminal()) {
                    lastReply = reply;
                }
            }

            @Override
            public Flow<T> asFlow() {
                if (lastReply != null) {
                    return super.asFlow()
                            .polyWith(Flow.just(lastReply.data()))
                            .flatMerge();
                }
                return super.asFlow();
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
            private List<Reply<? extends T>> replyBuffer = new ArrayList<>();

            @Override
            protected void onCollect(Reply<? extends T> reply) {
                super.onCollect(reply);
                replyBuffer.add(reply);
            }

            @Override
            public Flow<T> asFlow() {
                return Flow.<T>create(emitter -> {
                    for (Reply<? extends T> reply : replyBuffer) {
                        emitter.emit(reply);
                    }
                    emitter.complete();
                }).polyWith(super.asFlow()).flatConcat();
            }
        };
    }
}
