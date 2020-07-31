package cn.thens.jack.flow;


import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cn.thens.jack.func.Action0;
import cn.thens.jack.func.Action1;
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
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                onStart.run(emitter);
            }
        };
    }

    static <T> Flow<T> empty() {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) {
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> never() {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) {
            }
        };
    }

    static <T> Flow<T> error(final Throwable e) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) {
                emitter.error(e);
            }
        };
    }

    static <T> Flow<T> defer(final IFlow<T> flowFactory) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                flowFactory.asFlow().collectWith(emitter);
            }
        };
    }

    static <T> Flow<T> fromArray(T[] elements) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                for (T item : elements) {
                    emitter.data(item);
                }
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> fromIterable(Iterable<T> iterable) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                for (T item : iterable) {
                    emitter.data(item);
                }
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> fromFuture(Future<? extends T> future) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                emitter.data(future.get());
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> fromFunc(Func0<? extends T> func) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                emitter.data(func.call());
                emitter.complete();
            }
        };
    }

    static <T> Flow<T> fromAction(Action0 action) {
        return new Flow<T>() {
            @Override
            protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
                action.run();
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
        return new Flow<Integer>() {
            @Override
            protected void onStartCollect(Emitter<? super Integer> emitter) throws Throwable {
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

    private static Scheduler timerScheduler() {
        return Schedulers.timer();
    }

    static Flow<Long> timer(long delay, TimeUnit unit) {
        return new Flow<Long>() {
            @Override
            protected void onStartCollect(Emitter<? super Long> emitter) throws Throwable {
                emitter.addCancellable(timerScheduler().schedule(new Runnable() {
                    @Override
                    public void run() {
                        emitter.data(0L);
                        emitter.complete();
                    }
                }, delay, unit));
            }
        };
    }

    static Flow<Long> interval(long initialDelay, long period, TimeUnit unit) {
        return new Flow<Long>() {
            @Override
            protected void onStartCollect(Emitter<? super Long> emitter) throws Throwable {
                final AtomicLong count = new AtomicLong(0);
                emitter.addCancellable(timerScheduler().schedulePeriodically(new Runnable() {
                    @Override
                    public void run() {
                        emitter.data(count.getAndIncrement());
                    }
                }, initialDelay, period, unit));
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
                    emitter.data(bytes);
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
