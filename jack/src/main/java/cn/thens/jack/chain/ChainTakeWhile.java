package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

import cn.thens.jack.func.Predicate;

class ChainTakeWhile<T > extends Chain<T> {
    private final Chain<T> source;
    private final Predicate.X<? super T> predicateX;

    public ChainTakeWhile(Chain<T> source, Predicate<? super T> predicateX) {
        this.source = source;
        this.predicateX = Predicate.X.of(predicateX);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        Iterator<T> iterator = source.iterator();
        return new Iterator<T>() {
            int nextState = -1; // -1 for unknown, 0 for done, 1 for continue
            T nextItem = null;

            @Override
            public boolean hasNext() {
                if (nextState == -1) {
                    calcNext(); // will change nextState
                }
                return nextState == 1;
            }

            @Override
            public T next() {
                if (nextState == -1) {
                    calcNext(); // will change nextState
                }
                if (nextState == 0) {
                    throw new NoSuchElementException();
                }
                T result = nextItem;

                // Clean next to avoid keeping reference on yielded instance
                nextItem = null;
                nextState = -1;
                return result;
            }

            void calcNext() {
                if (iterator.hasNext()) {
                    T item = iterator.next();
                    if (predicateX.test(item)) {
                        nextState = 1;
                        nextItem = item;
                        return;
                    }
                }
                nextState = 0;
            }
        };
    }
}
