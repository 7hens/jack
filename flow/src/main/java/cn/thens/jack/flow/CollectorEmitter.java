package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.func.Values;
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
    private final BackPressure<T> backpressure;
    private final Cancellable cancellable = Cancellables.create();

    private CollectorEmitter(IScheduler scheduler, Collector<? super T> collector, BackPressure<T> backpressure) {
        this.scheduler = scheduler;
        this.collector = wrapCollector(collector);
        this.backpressure = backpressure;
    }

    @Override
    public void post(Reply<? extends T> reply) {
        if (isEmitterTerminated.compareAndSet(false, reply.isTerminal())) {
            try {
                if (reply.isCancel()) {
                    clearBuffer();
                }
                if (isCollecting.compareAndSet(false, true)) {
                    schedule(() -> {
                        try {
                            collector.post(reply);
                        } catch (Throwable e) {
                            throw Values.wrap(e);
                        }
                    });
                    return;
                }
                if (reply.isTerminal()) {
                    terminalReply = reply;
                    return;
                }
                buffer.add(reply.data());
                backpressure.apply(buffer);
            } catch (Throwable e) {
                error(e);
            }
        }
    }

    private void clearBuffer() {
        isEmitterTerminated.set(true);
        terminalReply = null;
        buffer.clear();
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

    static <T> CollectorEmitter<T> create(IScheduler scheduler, Collector<? super T> collector, BackPressure<T> backpressure) {
        return new CollectorEmitter<>(scheduler, collector, backpressure);
    }

    private static final BackPressure DEFAULT_BACKPRESSURE = BackPressures.success();

    @SuppressWarnings("unchecked")
    static <T> CollectorEmitter<T> create(IScheduler scheduler, Collector<? super T> collector) {
        return create(scheduler, collector, DEFAULT_BACKPRESSURE);
    }
}