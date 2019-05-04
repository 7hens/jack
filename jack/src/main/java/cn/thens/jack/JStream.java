package cn.thens.jack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "NullableProblems", "unused"})
public abstract class JStream<T> implements Iterable<T> {
    public static <T> JStream<T> of(Iterable<T> iterable) {
        return new JStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }

    @SafeVarargs
    public static <T> JStream<T> of(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return of(list);
    }

    public static <T> JStream<T> empty() {
        return of(new ArrayList<>());
    }

    public <C extends Collection<? super T>> C toCollection(C destination) {
        for (T item : this) {
            destination.add(item);
        }
        return destination;
    }

    public List<T> toList() {
        return toCollection(new ArrayList<>());
    }

    public HashSet<T> toHashSet() {
        return toCollection(new HashSet<>());
    }

    public Set<T> toSet() {
        return toCollection(new LinkedHashSet<>());
    }

    public boolean all(JFunc.F1<T, Boolean> predicate) {
        for (T item : this) {
            if (!predicate.call(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean any() {
        return iterator().hasNext();
    }

    public boolean any(JFunc.F1<T, Boolean> predicate) {
        for (T item : this) {
            if (predicate.call(item)) {
                return true;
            }
        }
        return false;
    }

    public int count() {
        int count = 0;
        for (T item : this) {
            count++;
        }
        return count;
    }

    public int count(JFunc.F1<T, Boolean> predicate) {
        int count = 0;
        for (T item : this) {
            if (predicate.call(item)) {
                count++;
            }
        }
        return count;
    }

    public <R> R fold(R initial, JFunc.F2<R, T, R> operation) {
        R accumulator = initial;
        for (T item : this) {
            accumulator = operation.call(accumulator, item);
        }
        return accumulator;
    }

    public <R> R foldIndexed(R initial, JFunc.F3<Integer, R, T, R> operation) {
        int index = 0;
        R accumulator = initial;
        for (T item : this) {
            accumulator = operation.call(index++, accumulator, item);
        }
        return accumulator;
    }

    public JStream<T> forEach(JFunc.A1<T> func) {
        for (T item : this) {
            func.run(item);
        }
        return this;
    }

    public JStream<T> forEachIndexed(JFunc.A2<Integer, T> func) {
        int index = 0;
        for (T item : this) {
            func.run(index++, item);
        }
        return this;
    }

    public <R> JStream<R> map(JFunc.F1<T, R> func) {
        return new TransformingStream<>(this, func);
    }

    public <R> JStream<R> mapIndexed(JFunc.F2<Integer, T, R> func) {
        return new TransformingIndexedStream<>(this, func);
    }

    public JStream<T> filter(JFunc.F1<T, Boolean> predicate) {
        return new FilteringStream<>(this, predicate);
    }

    public JStream<T> filterIndexed(JFunc.F2<Integer, T, Boolean> predicate) {
        return new TransformingStream<>(new FilteringStream<>(
                new IndexingStream<>(this),
                it -> predicate.call(it.index, it.value)
        ), it -> it.value);
    }

    public <K> JStream<T> distinctBy(JFunc.F1<T, K> keySelector) {
        return new DistinctStream<>(this, keySelector);
    }

    public JStream<T> distinct() {
        return distinctBy(it -> it);
    }

    private static class IndexedValue<T> {
        private final int index;
        private final T value;

        private IndexedValue(int index, T value) {
            this.index = index;
            this.value = value;
        }
    }

    private static class IndexingStream<T> extends JStream<IndexedValue<T>> {
        private final JStream<T> stream;

        private IndexingStream(JStream<T> stream) {
            this.stream = stream;
        }

        @Override
        public Iterator<IndexedValue<T>> iterator() {
            return new Iterator<IndexedValue<T>>() {
                Iterator<T> iterator = stream.iterator();
                int index = 0;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public IndexedValue<T> next() {
                    return new IndexedValue<>(index++, iterator.next());
                }
            };
        }
    }

    private static class TransformingStream<T, R> extends JStream<R> {
        private final JStream<T> stream;
        private final JFunc.F1<T, R> transformer;

        private TransformingStream(JStream<T> stream, JFunc.F1<T, R> transformer) {
            this.stream = stream;
            this.transformer = transformer;
        }

        @Override
        public Iterator<R> iterator() {
            Iterator<T> iterator = stream.iterator();
            return new Iterator<R>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public R next() {
                    return transformer.call(iterator.next());
                }
            };
        }
    }

    private static class TransformingIndexedStream<T, R> extends JStream<R> {
        private final JStream<T> stream;
        private final JFunc.F2<Integer, T, R> transformer;

        private TransformingIndexedStream(JStream<T> stream, JFunc.F2<Integer, T, R> transformer) {
            this.stream = stream;
            this.transformer = transformer;
        }

        @Override
        public Iterator<R> iterator() {
            Iterator<T> iterator = stream.iterator();
            final int[] index = {0};
            return new Iterator<R>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public R next() {
                    return transformer.call(index[0]++, iterator.next());
                }
            };
        }
    }

    private static class FilteringStream<T> extends JStream<T> {
        private final JStream<T> stream;
        private final boolean sendWhen;
        private final JFunc.F1<T, Boolean> predicate;

        private FilteringStream(JStream<T> stream, boolean sendWhen, JFunc.F1<T, Boolean> predicate) {
            this.stream = stream;
            this.sendWhen = sendWhen;
            this.predicate = predicate;
        }

        private FilteringStream(JStream<T> stream, JFunc.F1<T, Boolean> predicate) {
            this(stream, true, predicate);
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                Iterator<T> iterator = stream.iterator();
                int nextState = -1; // -1 for unknown, 0 for done, 1 for continue
                T nextItem = null;

                void calcNext() {
                    while (iterator.hasNext()) {
                        T item = iterator.next();
                        if (predicate.call(item) == sendWhen) {
                            nextItem = item;
                            nextState = 1;
                            return;
                        }
                    }
                    nextState = 0;
                }

                @Override
                public boolean hasNext() {
                    if (nextState == -1) {
                        calcNext();
                    }
                    return nextState == 1;
                }

                @Override
                public T next() {
                    if (nextState == -1) {
                        calcNext();
                    }
                    if (nextState == 0) {
                        throw new NoSuchElementException();
                    }
                    T result = nextItem;
                    nextItem = null;
                    nextState = -1;
                    return result;
                }
            };
        }
    }

    private static abstract class AbstractIterator<T> implements Iterator<T> {
        private enum State {READY, NOT_READY, DONE, FAILED}

        private State state = State.NOT_READY;
        private T nextValue;

        protected abstract void computeNext();

        @Override
        public boolean hasNext() {
            if (state == State.FAILED) throw new AssertionError();
            switch (state) {
                case DONE:
                    return false;
                case READY:
                    return true;
                default:
                    return tryToComputeNext();
            }
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            state = State.NOT_READY;
            return nextValue;
        }

        private boolean tryToComputeNext() {
            state = State.FAILED;
            computeNext();
            return state == State.READY;
        }

        protected void setNext(T value) {
            nextValue = value;
            state = State.READY;
        }

        protected void done() {
            state = State.DONE;
        }
    }

    private static class DistinctStream<T, K> extends JStream<T> {
        private final JStream<T> source;
        private final JFunc.F1<T, K> keySelector;

        private DistinctStream(JStream<T> source, JFunc.F1<T, K> keySelector) {
            this.source = source;
            this.keySelector = keySelector;
        }

        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
                HashSet<K> observed = new HashSet<>();
                Iterator<T> sourceIterator = source.iterator();

                @Override
                protected void computeNext() {
                    while (sourceIterator.hasNext()) {
                        T next = sourceIterator.next();
                        K key = keySelector.call(next);
                        if (observed.add(key)) {
                            setNext(next);
                            return;
                        }
                    }
                    done();
                }
            };
        }
    }
}
