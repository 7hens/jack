package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import cn.thens.jack.func.Predicate;

class ChainTakeUtil<T> extends Chain<T> {
    private final Chain<T> up;
    private final Predicate<T> predicate;

    ChainTakeUtil(Chain<T> up, Predicate<T> predicate) {
        this.up = up;
        this.predicate = predicate;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        Iterator<T> iterator = up.iterator();
        return new AbstractIterator<T>() {
            boolean isDone = false;

            @Override
            protected void computeNext() throws Throwable {
                if (!isDone && iterator.hasNext()) {
                    T value = iterator.next();
                    setNext(value);
                    if (predicate.test(value)) {
                        isDone = true;
                    }
                    return;
                }
                done();
            }
        };
    }
}
