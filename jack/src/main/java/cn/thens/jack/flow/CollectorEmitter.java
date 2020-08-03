package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.func.Things;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Cancellables;
import cn.thens.jack.scheduler.ICancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
class CollectorEmitter<T> implements Emitter<T> {
    private final AtomicBoolean isCollecting = new AtomicBoolean(false);
    private final AtomicBoolean isCollectorTerminated = new AtomicBoolean(false);
    private final AtomicBoolean isEmitterTerminated = new AtomicBoolean(false);
    private final List<T> buffer = new CopyOnWriteArrayList<>();
    private Reply<? extends T> terminalReply = null;

    private final IScheduler scheduler;
    private final Collector<? super T> collector;
    private final Backpressure<T> backpressure;
    private final Cancellable cancellable = Cancellables.create();

    private CollectorEmitter(IScheduler scheduler, Collector<? super T> collector, Backpressure<T> backpressure) {
        this.scheduler = scheduler;
        this.collector = wrapCollector(collector);
        this.backpressure = backpressure;
    }

    @Override
    public void post(Reply<? extends T> reply) {
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
        schedule(() -> {
            try {
                collector.post(reply);
            } catch (Throwable e) {
                throw Things.wrap(e);
            }
        });
    }

    private Collector<T> wrapCollector(Collector<? super T> collector) {
        return new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) throws Throwable {
                isCollecting.set(true);
                if (isCollectorTerminated.compareAndSet(false, reply.isTerminal())) {
                    collector.post(reply);
                    if (reply.isTerminal()) {
                        clearBuffer();
                        cancellable.cancel();
                        return;
                    }
                    if (!buffer.isEmpty()) {
                        post(Reply.data(buffer.remove(0)));
                    } else if (terminalReply != null) {
                        post(terminalReply);
                    } else {
                        isCollecting.set(false);
                    }
                }
            }
        };
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
        return isEmitterTerminated.get();
    }

    @Override
    public void addCancellable(ICancellable onCancel) {
        cancellable.addCancellable(onCancel);
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        return scheduler.schedule(runnable);
    }

    static <T> CollectorEmitter<T> create(IScheduler scheduler, Collector<? super T> collector, Backpressure<T> backpressure) {
        return new CollectorEmitter<>(scheduler, collector, backpressure);
    }

    private static final Backpressure DEFAULT_BACKPRESSURE = Backpressure.success();

    @SuppressWarnings("unchecked")
    static <T> CollectorEmitter<T> create(IScheduler scheduler, Collector<? super T> collector) {
        return create(scheduler, collector, DEFAULT_BACKPRESSURE);
    }
}
