package cn.thens.jack.flow;

import java.nio.BufferOverflowException;
import java.util.LinkedList;

import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;

@SuppressWarnings("WeakerAccess")
public abstract class Backpressure<T> {
    abstract void apply(LinkedList<T> buffer) throws Throwable;

    @Deprecated
    public final Backpressure<T> catchError(Func1<? super Throwable, ? extends Backpressure<T>> catchError) {
        return or(catchError);
    }

    @Deprecated
    public final Backpressure<T> catchError(Backpressure<T> backpressure) {
        return or(backpressure);
    }

    public final Backpressure<T> or(Func1<? super Throwable, ? extends Backpressure<T>> catchError) {
        Backpressure<T> self = this;
        return new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) throws Throwable {
                try {
                    self.apply(buffer);
                } catch (Throwable e) {
                    catchError.invoke(e).apply(buffer);
                }
            }
        };
    }

    public final Backpressure<T> or(Backpressure<T> backpressure) {
        return or(Funcs.always(backpressure));
    }

    public final Backpressure<T> dropAll() {
        return or(new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) throws Throwable {
                buffer.clear();
            }
        });
    }

    public final Backpressure<T> dropLatest() {
        return or(new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) throws Throwable {
                buffer.removeLast();
            }
        });
    }

    public final Backpressure<T> dropOldest() {
        return or(new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) throws Throwable {
                buffer.removeFirst();
            }
        });
    }

    public static <T> Backpressure<T> buffer(int capacity) {
        return new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) {
                if (buffer.size() > capacity) {
                    throw new BufferOverflowException();
                }
            }
        };
    }

    public static <T> Backpressure<T> error() {
        return new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) throws Throwable {
                throw new BufferOverflowException();
            }
        };
    }

    @Deprecated
    public static <T> Backpressure<T> none() {
        return success();
    }

    public static <T> Backpressure<T> success() {
        return new Backpressure<T>() {
            @Override
            void apply(LinkedList<T> buffer) throws Throwable {
            }
        };
    }
}
