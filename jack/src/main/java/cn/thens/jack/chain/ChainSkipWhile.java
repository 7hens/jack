package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import cn.thens.jack.func.Predicate;

class ChainSkipWhile<T> extends Chain<T> {
    private final Chain<T> source;
    private final Predicate.X<T> predicate;

    public ChainSkipWhile(Chain<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = Predicate.X.of(predicate);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        Iterator<T> iterator = source.iterator();
        return new Iterator<T>() {
            int dropState = -1;
            // -1 for not dropping, 1 for nextItem, 0 for normal iteration
            T nextItem = null;

            @Override
            public boolean hasNext() {
                if (dropState == -1) {
                    drop();
                }
                return dropState == 1 || iterator.hasNext();
            }

            @Override
            public T next() {
                if (dropState == -1) {
                    drop();
                }

                if (dropState == 1) {
                    T result = nextItem;
                    nextItem = null;
                    dropState = 0;
                    return result;
                }
                return iterator.next();
            }

            void drop() {
                while (iterator.hasNext()) {
                    T item = iterator.next();
                    if (!predicate.test(item)) {
                        nextItem = item;
                        dropState = 1;
                        return;
                    }
                }
                dropState = 0;
            }
        };
    }
}
