package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.scheduler.CancellableScheduler;
import cn.thens.jack.scheduler.ICancellable;
import cn.thens.jack.scheduler.Scheduler;

/**
 * @author 7hens
 */
class CollectorEmitter<T> implements Emitter<T>, Collector<T> {
    private final AtomicBoolean isCollecting = new AtomicBoolean(false);
    private final AtomicBoolean isCollectorTerminated = new AtomicBoolean(false);
    private final AtomicBoolean isEmitterTerminated = new AtomicBoolean(false);
    private final List<T> buffer = new CopyOnWriteArrayList<>();
    private Reply<? extends T> terminalReply = null;

    private final CancellableScheduler scheduler;
    private final Collector<? super T> collector;
    private final Backpressure<T> backpressure;

    private CollectorEmitter(Scheduler scheduler, Collector<? super T> collector, Backpressure<T> backpressure) {
        this.scheduler = scheduler.cancellable();
        this.collector = wrapCollector(collector);
        this.backpressure = backpressure;
    }

    @Override
    public void emit(Reply<? extends T> reply) {
        onCollect(reply);
    }

    @Override
    public void onCollect(Reply<? extends T> reply) {
        if (isEmitterTerminated.compareAndSet(false, reply.isTerminal())) {
            if (reply.isCancel()) {
                clearBuffer();
                collectScheduled(reply);
                return;
            }
            if (isCollecting.compareAndSet(false, true)) {
                collectScheduled(reply);
                return;
            }
            if (reply.isTerminal()) {
                terminalReply = reply;
                return;
            }
            try {
                buffer.add(reply.data());
                backpressure.apply(buffer);
            } catch (Throwable e) {
                clearBuffer();
                collectScheduled(Reply.error(e));
//                error(e);
            }
        }
    }

    private void clearBuffer() {
        isEmitterTerminated.set(true);
        terminalReply = null;
        buffer.clear();
    }

    private void collectScheduled(Reply<? extends T> reply) {
        scheduler.schedule(() -> collector.onCollect(reply));
    }

    private Collector<T> wrapCollector(Collector<? super T> collector) {
        return new Collector<T>() {
            @Override
            public void onCollect(Reply<? extends T> reply) {
                isCollecting.set(true);
                if (isCollectorTerminated.compareAndSet(false, reply.isTerminal())) {
                    collector.onCollect(reply);
                    if (reply.isTerminal()) {
                        clearBuffer();
                        scheduler.cancel();
                        return;
                    }
                    if (!buffer.isEmpty()) {
                        onCollect(Reply.data(buffer.remove(0)));
                    } else if (terminalReply != null) {
                        onCollect(terminalReply);
                    } else {
                        isCollecting.set(false);
                    }
                }
            }
        };
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
    public boolean isCancelled() {
        return isEmitterTerminated.get();
    }

    @Override
    public Scheduler scheduler() {
        return scheduler.parent();
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        scheduler.addCancellable(onCancel);
    }

    static <T> CollectorEmitter<T> create(Scheduler scheduler, Collector<? super T> collector, Backpressure<T> backpressure) {
        return new CollectorEmitter<>(scheduler, collector, backpressure);
    }

    private static final Backpressure DEFAULT_BACKPRESSURE = Backpressure.success();

    @SuppressWarnings("unchecked")
    static <T> CollectorEmitter<T> create(Scheduler scheduler, Collector<? super T> collector) {
        return create(scheduler, collector, DEFAULT_BACKPRESSURE);
    }
}
