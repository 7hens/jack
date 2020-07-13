package cn.thens.jack.flow;


import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.func.Action1;
import cn.thens.jack.scheduler.Scheduler;
import cn.thens.jack.scheduler.Schedulers;


/**
 * @author 7hens
 */
final class FlowCreate {
    static <T> Flow<T> create(Action1<? super Emitter<? super T>> onStart) {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) throws Throwable {
                onStart.run(emitter);
            }
        };
    }

    static <T> Flow<T> empty() {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) {
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> never() {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) {
            }
        };
    }

    static <T> Flow<T> error(final Throwable e) {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) {
                emitter.error(e);
            }
        };
    }

    static <T> Flow<T> defer(final IFlow<T> flowFactory) {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) throws Throwable {
                flowFactory.asFlow().collect(emitter);
            }
        };
    }

    static <T> Flow<T> fromArray(T[] elements) {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) throws Throwable {
                for (T item : elements) {
                    emitter.data(item);
                }
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> fromIterable(Iterable<T> iterable) {
        return new AbstractFlow<T>() {
            @Override
            protected void onStart(CollectorEmitter<? super T> emitter) throws Throwable {
                for (T item : iterable) {
                    emitter.data(item);
                }
                emitter.complete();
            }
        };
    }

    static Flow<Integer> range(int start, int end, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be zero");
        }
        if ((end - start) * step < 0) {
            throw new IllegalArgumentException("Illegal range " + start + "~" + end + ", " + step);
        }
        return new AbstractFlow<Integer>() {
            @Override
            protected void onStart(CollectorEmitter<? super Integer> emitter) throws Throwable {
                if (end > start) {
                    for (int i = start; i <= end; i += step) {
                        emitter.data(i);
                    }
                } else {
                    for (int i = start; i >= end; i += step) {
                        emitter.data(i);
                    }
                }
                emitter.complete();
            }
        };
    }

    private static Scheduler getDelayedScheduler(Scheduler scheduler) {
        return scheduler != Schedulers.unconfined() ? scheduler : Schedulers.single();
    }

    static Flow<Long> timer(long delay, TimeUnit unit) {
        return new AbstractFlow<Long>() {
            @Override
            protected void onStart(CollectorEmitter<? super Long> emitter) throws Throwable {
                getDelayedScheduler(emitter.scheduler()).schedule(new Runnable() {
                    @Override
                    public void run() {
                        emitter.data(0L);
                        emitter.complete();
                    }
                }, delay, unit);
            }
        };
    }

    static Flow<Long> interval(long initialDelay, long period, TimeUnit unit) {
        return new AbstractFlow<Long>() {
            @Override
            protected void onStart(CollectorEmitter<? super Long> emitter) throws Throwable {
                final AtomicLong count = new AtomicLong(0);
                getDelayedScheduler(emitter.scheduler()).schedulePeriodically(new Runnable() {
                    @Override
                    public void run() {
                        emitter.data(count.getAndIncrement());
                    }
                }, initialDelay, period, unit);
            }
        };
    }

    static Flow<Integer> from(@NotNull InputStream input, byte[] buffer) {
        return create(emitter -> {
            try {
                int bytes = input.read(buffer);
                while (bytes >= 0) {
                    emitter.data(bytes);
                    bytes = input.read(buffer);
                }
            } catch (Throwable e) {
                emitter.error(e);
                return;
            } finally {
                input.close();
            }
            emitter.complete();
        });
    }

    static Flow<Integer> copy(@NotNull InputStream input, @NotNull OutputStream output, byte[] buffer) {
        return from(input, buffer)
                .onEach(bytes -> output.write(buffer, 0, bytes))
                .onTerminate(it -> output.close());
    }
}
