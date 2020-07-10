package cn.thens.jack.flow;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
class FlowTimeout<T> implements FlowOperator<T, T> {
    private final long timeout;
    private final TimeUnit unit;
    private final IFlow<T> fallback;

    FlowTimeout(long timeout, TimeUnit unit, IFlow<T> fallback) {
        this.timeout = timeout;
        this.unit = unit;
        this.fallback = fallback;
    }

    @Override
    public Collector<T> apply(Emitter<? super T> emitter) {
        return new MyCollector(emitter);
    }

    private class MyCollector implements Collector<T>, Runnable {
        private final AtomicBoolean isTransferred = new AtomicBoolean(false);
        private final Emitter<? super T> emitter;
        private Cancellable cancellable;

        MyCollector(Emitter<? super T> emitter) {
            this.emitter = emitter;
            cancellable = emitter.scheduler().schedule(this, timeout, unit);
        }

        @Override
        public void onCollect(Reply<? extends T> reply) {
            if (isTransferred.get()) return;
            emitter.emit(reply);
            if (cancellable != null) {
                cancellable.cancel();
            }
            if (reply.isTerminal() || emitter.isTerminated()) {
                return;
            }
            cancellable = emitter.scheduler().schedule(this, timeout, unit);
        }

        @Override
        public void run() {
            if (isTransferred.compareAndSet(false, true)) {
                try {
                    fallback.asFlow().collect(emitter, new Collector<T>() {
                        @Override
                        public void onCollect(Reply<? extends T> reply) {
                            emitter.emit(reply);
                        }
                    });
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        }
    }
}
