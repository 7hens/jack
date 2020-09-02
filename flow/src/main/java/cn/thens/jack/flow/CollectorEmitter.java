package cn.thens.jack.flow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Cancellables;
import cn.thens.jack.scheduler.ICancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
class CollectorEmitter<T> implements Emitter<T>, Runnable {
    private final AtomicBoolean isCollectorTerminated = new AtomicBoolean(false);
    private final AtomicBoolean isEmitterTerminated = new AtomicBoolean(false);
    private final List<T> buffer = new CopyOnWriteArrayList<>();
    private Reply<? extends T> terminalReply = null;
    private final AtomicInteger bufferSize = new AtomicInteger();

    private final IScheduler scheduler;
    private final Collector<? super T> collector;
    private final BackPressure<T> backPressure;
    private final Cancellable cancellable = Cancellables.create();

    private CollectorEmitter(IScheduler scheduler, Collector<? super T> collector, BackPressure<T> backPressure) {
        this.scheduler = scheduler;
        this.collector = collector;
        this.backPressure = backPressure;
    }

    @Override
    public void post(Reply<? extends T> reply) {
        boolean isTerminal = reply.isTerminal();
        if (isEmitterTerminated.compareAndSet(false, isTerminal)) {
            try {
                if (reply.isCancel()) {
                    clearBuffer();
                }
                if (isTerminal) {
                    terminalReply = reply;
                } else {
                    buffer.add(reply.data());
                    backPressure.apply(buffer);
                }
                if (bufferSize.getAndIncrement() == 0) {
                    schedule(this);
                }
            } catch (Throwable e) {
                error(e);
            }
        }
    }

    private void clearBuffer() {
        bufferSize.set(0);
        buffer.clear();
        terminalReply = null;
    }

    @Override
    public void run() {
        try {
            while (bufferSize.get() > 0) {
                if (!buffer.isEmpty()) {
                    handle(Reply.next(buffer.remove(0)));
                } else if (terminalReply != null) {
                    handle(terminalReply);
                }
                if (bufferSize.get() == 0 || bufferSize.decrementAndGet() == 0) {
                    break;
                }
            }
        } catch (Throwable e) {
            clearBuffer();
            error(e);
        }
    }

    private void handle(Reply<? extends T> reply) throws Throwable {
        boolean isTerminal = reply.isTerminal();
        if (isCollectorTerminated.compareAndSet(false, isTerminal)) {
            collector.post(reply);
            if (isTerminal) {
                clearBuffer();
                cancellable.cancel();
            }
        }
    }

    @Override
    public void next(T data) {
        post(Reply.next(data));
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
    public void into(Cancellable cancellable) {
        cancellable.addCancellable(this);
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        return scheduler.schedule(runnable);
    }

    static <T> CollectorEmitter<T> create(IScheduler scheduler, Collector<? super T> collector, BackPressure<T> backPressure) {
        return new CollectorEmitter<>(scheduler, collector, backPressure);
    }

    @SuppressWarnings("rawtypes")
    private static final BackPressure DEFAULT_BACK_PRESSURE = BackPressures.success();

    @SuppressWarnings("unchecked")
    static <T> CollectorEmitter<T> create(IScheduler scheduler, Collector<? super T> collector) {
        return create(scheduler, collector, DEFAULT_BACK_PRESSURE);
    }
}
