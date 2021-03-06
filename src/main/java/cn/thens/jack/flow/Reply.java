package cn.thens.jack.flow;

import java.util.concurrent.CancellationException;

import cn.thens.jack.func.Values;

/**
 * @author 7hens
 */
public abstract class Reply<T> {
    public abstract boolean isTerminal();

    public abstract Throwable error();

    public abstract T next();

    public final boolean isComplete() {
        return isTerminal() && error() == null;
    }

    public final boolean isError() {
        Throwable error = error();
        return isTerminal() && error != null && !(error instanceof CancellationException);
    }

    public final boolean isCancel() {
        return isTerminal() && error() instanceof CancellationException;
    }

    public final <R> Reply<R> newReply(final R data) {
        Reply<T> self = this;
        return new Reply<R>() {
            @Override
            public boolean isTerminal() {
                return self.isTerminal();
            }

            @Override
            public Throwable error() {
                return self.error();
            }

            @Override
            public R next() {
                return data;
            }
        };
    }

    public static <T> Reply<T> next(final T data) {
        return new Reply<T>() {
            @Override
            public boolean isTerminal() {
                return false;
            }

            @Override
            public Throwable error() {
                return null;
            }

            @Override
            public T next() {
                return data;
            }
        };
    }

    public static <T> Reply<T> error(final Throwable error) {
        final Throwable cause = error != null ? Values.unwrap(error) : null;
        return new Reply<T>() {
            @Override
            public boolean isTerminal() {
                return true;
            }

            @Override
            public Throwable error() {
                return cause;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }

    public static <T> Reply<T> cancel() {
        return error(new CancellationException());
    }

    private static final Reply COMPLETE = error(null);

    @SuppressWarnings("unchecked")
    public static <T> Reply<T> complete() {
        return COMPLETE;
    }
}
