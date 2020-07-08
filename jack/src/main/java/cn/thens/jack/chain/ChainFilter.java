package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

import cn.thens.jack.func.Predicate;
import cn.thens.jack.ref.Ref;

class ChainFilter<T> extends Chain<T> {
    private static final int STATE_UNKNOWN = -1;
    private static final int STATE_DONE = 0;
    private static final int STATE_CONTINUE = 1;

    private final Chain<T> up;
    private final Predicate.X<T> predicate;

    ChainFilter(Chain<T> up, Predicate<T> predicate) {
        this.up = up;
        this.predicate = Predicate.X.of(predicate);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<T>() {
            Iterator<T> iterator = up.iterator();
            int nextState = STATE_UNKNOWN;
            T nextItem = null;

            @Override
            public boolean hasNext() {
                calcNext();
                return nextState == STATE_CONTINUE;
            }

            @Override
            public T next() {
                calcNext();
                if (nextState == STATE_DONE) {
                    throw new NoSuchElementException();
                }
                T result = nextItem;
                nextItem = null;
                nextState = STATE_UNKNOWN;
                return result;
            }

            void calcNext() {
                if (nextState != STATE_UNKNOWN) return;
                while (iterator.hasNext()) {
                    T item = iterator.next();
                    if (predicate.test(item)) {
                        nextItem = item;
                        nextState = STATE_CONTINUE;
                        return;
                    }
                }
                nextState = STATE_DONE;
            }
        };
    }

    static <T> Chain<T> filter(Chain<T> up, Predicate<T> predicate) {
        return new ChainFilter<>(up, predicate);
    }

    static <T> Chain<T> skip(Chain<T> up, int n) {
        return filter(up, Predicate.X.skip(n));
    }
}
