package cn.thens.jack.flow;


import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.func.Action0;
import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Actions;
import cn.thens.jack.func.Func0;
import cn.thens.jack.scheduler.Scheduler;
import cn.thens.jack.scheduler.Schedulers;


/**
 * @author 7hens
 */
final class FlowCreate {
    static <T> Flow<T> create(Action1<? super Emitter<? super T>> onStart) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) {
                try {
                    onStart.run(emitter);
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        };
    }

    static <T> Flow<T> empty() {
        return create(Emitter::complete);
    }

    static <T> Flow<T> never() {
        return create(Actions.empty());
    }

    static <T> Flow<T> error(final Throwable e) {
        return create(emitter -> emitter.error(e));
    }

    static <T> Flow<T> defer(final IFlow<T> flowFactory) {
        return create(emitter -> {
            flowFactory.asFlow().onStartCollect(emitter);
        });
    }

    static <T> Flow<T> fromArray(T[] elements) {
        return create(emitter -> {
            for (T item : elements) {
                emitter.next(item);
            }
            emitter.complete();
        });
    }

    static <T> Flow<T> fromIterable(Iterable<T> iterable) {
        return create(emitter -> {
            for (T item : iterable) {
                emitter.next(item);
            }
            emitter.complete();
        });
    }

    static <T> Flow<T> fromFuture(Future<? extends T> future) {
        return create(emitter -> {
            emitter.next(future.get());
            emitter.complete();
        });
    }

    static <T> Flow<T> fromFunc(Func0<? extends T> func) {
        return create(emitter -> {
            emitter.next(func.call());
            emitter.complete();
        });
    }

    static <T> Flow<T> fromAction(Action0 action) {
        return create(emitter -> {
            action.run();
            emitter.complete();
        });
    }

    static Flow<Integer> range(int start, int end, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be zero");
        }
        if ((end - start) * step < 0) {
            throw new IllegalArgumentException("Illegal range " + start + "~" + end + ", " + step);
        }
        return create(emitter -> {
            if (end > start) {
                for (int i = start; i <= end; i += step) {
                    emitter.next(i);
                }
            } else {
                for (int i = start; i >= end; i += step) {
                    emitter.next(i);
                }
            }
            emitter.complete();
        });
    }

    private static Scheduler schedulerOf(Emitter<?> emitter) {
        return Schedulers.timer();
    }

    static Flow<Long> timer(long delay, TimeUnit unit) {
        return create(emitter -> {
            emitter.addCancellable(schedulerOf(emitter).schedule(() -> {
                emitter.next(0L);
                emitter.complete();
            }, delay, unit));
        });
    }

    static Flow<Long> interval(long initialDelay, long period, TimeUnit unit) {
        return create(emitter -> {
            final AtomicLong count = new AtomicLong(0);
            emitter.addCancellable(schedulerOf(emitter).schedulePeriodically(() ->
                    emitter.next(count.getAndIncrement()), initialDelay, period, unit));
        });
    }

    static Flow<Integer> from(@NotNull InputStream input, byte[] buffer) {
        return create(emitter -> {
            try {
                int bytes = input.read(buffer);
                while (bytes >= 0) {
                    emitter.next(bytes);
                    bytes = input.read(buffer);
                }
                emitter.complete();
            } catch (Throwable e) {
                emitter.error(e);
            } finally {
                input.close();
            }
        });
    }

    static Flow<Integer> copy(@NotNull InputStream input, @NotNull OutputStream output, byte[] buffer) {
        return create(emitter -> {
            try {
                int bytes = input.read(buffer);
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes);
                    emitter.next(bytes);
                    bytes = input.read(buffer);
                }
                emitter.complete();
            } catch (Throwable e) {
                emitter.error(e);
            } finally {
                input.close();
                output.close();
            }
        });
    }
}
