package cn.thens.jack.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import cn.thens.jack.func.JAction;
import cn.thens.jack.func.JFunc;

@SuppressWarnings({"WeakerAccess", "NullableProblems", "unused", "unchecked"})
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

    private static Iterator EMPTY_ITERATOR = new Iterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
    };

    private static JStream EMPTY = of(EMPTY_ITERATOR);

    public static <T> JStream<T> empty() {
        return EMPTY;
    }

    public JStream<T> onEach(JAction.T1<T> func) {
        return map(it -> {
            func.call(it);
            return it;
        });
    }

    public JStream<T> onEachIndexed(JAction.T2<Integer, T> func) {
        return mapIndexed((index, value) -> {
            func.call(index, value);
            return value;
        });
    }

    public JStream<T> requireNoNulls() {
        return map(it -> {
            if (it == null) throw new IllegalArgumentException("null element found in " + this);
            return it;
        });
    }

    public <R, V> JStream<V> zip(JStream<R> other, JFunc.T2<T, R, V> transform) {
        JStream<T> source = this;
        return new JStream<V>() {
            @Override
            public Iterator<V> iterator() {
                Iterator<T> sourceIterator = source.iterator();
                Iterator<R> otherIterator = other.iterator();
                return new Iterator<V>() {
                    @Override
                    public boolean hasNext() {
                        return sourceIterator.hasNext() && otherIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return transform.call(sourceIterator.next(), otherIterator.next());
                    }
                };
            }
        };
    }

    public <R> JStream<R> zipWithNext(JFunc.T2<T, T, R> transform) {
        JStream<T> source = this;
        return new JStream<R>() {
            @Override
            public Iterator<R> iterator() {
                Iterator<T> firstIterator = source.iterator();
                Iterator<T> secondIterator = source.iterator();
                if (secondIterator.hasNext()) secondIterator.next();
                if (!secondIterator.hasNext()) return EMPTY_ITERATOR;
                return new Iterator<R>() {
                    @Override
                    public boolean hasNext() {
                        return secondIterator.hasNext();
                    }

                    @Override
                    public R next() {
                        return transform.call(firstIterator.next(), secondIterator.next());
                    }
                };
            }
        };
    }

    public <R> JStream<R> map(JFunc.T1<T, R> transformer) {
        JStream<T> source = this;
        return new JStream<R>() {
            @Override
            public Iterator<R> iterator() {
                Iterator<T> iterator = source.iterator();
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
        };
    }

    public <R> JStream<R> mapIndexed(JFunc.T2<Integer, T, R> transformer) {
        JStream<T> source = this;
        return new JStream<R>() {
            @Override
            public Iterator<R> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<R>() {
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public R next() {
                        return transformer.call(index++, iterator.next());
                    }
                };
            }
        };
    }

    public JStream<T> filter(JFunc.T1<T, Boolean> predicate) {
        JStream<T> source = this;
        return new JStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    Iterator<T> iterator = source.iterator();
                    int nextState = -1; // -1 for unknown, 0 for done, 1 for continue
                    T nextItem = null;

                    void calcNext() {
                        while (iterator.hasNext()) {
                            T item = iterator.next();
                            if (predicate.call(item)) {
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
        };
    }

    private JStream<IndexedValue<T>> indexed() {
        JStream<T> source = this;
        return new JStream<IndexedValue<T>>() {
            @Override
            public Iterator<IndexedValue<T>> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<IndexedValue<T>>() {
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
        };
    }

    public JStream<T> filterIndexed(JFunc.T2<Integer, T, Boolean> predicate) {
        return indexed()
                .filter(it -> predicate.call(it.index, it.value))
                .map(it -> it.value);
    }

    public <K> JStream<T> distinctBy(JFunc.T1<T, K> keySelector) {
        JStream<T> source = this;
        return new JStream<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T> iterator = source.iterator();
                HashSet<K> observed = new HashSet<>();
                return new AbstractIterator<T>() {
                    @Override
                    protected void computeNext() {
                        while (iterator.hasNext()) {
                            T next = iterator.next();
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
        };
    }

    public JStream<T> distinct() {
        return distinctBy(it -> it);
    }

    private static class IndexedValue<T> {
        final int index;
        final T value;

        IndexedValue(int index, T value) {
            this.index = index;
            this.value = value;
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

    public <U> U call(JFunc.T1<JStream<T>, U> func) {
        return func.call(this);
    }


    public void forEach(JAction.T1<T> func) {
        for (T item : this) {
            func.call(item);
        }
    }

    public void forEachIndexed(JAction.T2<Integer, T> func) {
        int index = 0;
        for (T item : this) {
            func.call(index++, item);
        }
    }

    public int count() {
        int count = 0;
        for (T item : this) {
            count++;
        }
        return count;
    }

    public int count(JFunc.T1<T, Boolean> predicate) {
        int count = 0;
        for (T item : this) {
            if (predicate.call(item)) {
                count++;
            }
        }
        return count;
    }

    public int indexOf(T element) {
        int index = 0;
        for (T item : this) {
            if (element == item) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public int lastIndexOf(T element) {
        int lastIndex = -1;
        int index = 0;
        for (T item : this) {
            if (element == item) {
                lastIndex = index;
            }
            index++;
        }
        return lastIndex;
    }

    public boolean contains(T element) {
        return indexOf(element) >= 0;
    }

    public T elementAtOrElse(int index, JFunc.T1<Integer, T> defaultValue) {
        if (index < 0) {
            return defaultValue.call(index);
        }
        Iterator<T> iterator = iterator();
        int count = 0;
        while (iterator.hasNext()) {
            T element = iterator.next();
            if (index == count++) {
                return element;
            }
        }
        return defaultValue.call(index);
    }

    public T elementAtOrNull(int index) {
        return elementAtOrElse(index, it -> null);
    }

    public T elementAt(int index) {
        return elementAtOrElse(index, it -> {
            throw new IndexOutOfBoundsException("Stream doesn't contain element at index " + index);
        });
    }

    public T firstOrElse(JFunc.T1<T, Boolean> predicate, JFunc.T0<T> defaultValue) {
        for (T item : this) {
            if (predicate.call(item)) return item;
        }
        return defaultValue.call();
    }

    public T firstOrNull(JFunc.T1<T, Boolean> predicate) {
        return firstOrElse(predicate, () -> null);
    }

    public T first(JFunc.T1<T, Boolean> predicate) {
        return firstOrElse(predicate, () -> {
            throw new NoSuchElementException("Stream contains no element matching the predicate.");
        });
    }

    public T lastOrElse(JFunc.T1<T, Boolean> predicate, JFunc.T0<T> defaultValue) {
        T last = null;
        boolean found = false;
        for (T item : this) {
            if (predicate.call(item)) {
                found = true;
                last = item;
            }
        }
        return found ? last : defaultValue.call();
    }

    public T lastOrNull(JFunc.T1<T, Boolean> predicate) {
        return lastOrElse(predicate, () -> null);
    }

    public T last(JFunc.T1<T, Boolean> predicate) {
        return lastOrElse(predicate, () -> {
            throw new NoSuchElementException("Stream contains no element matching the predicate.");
        });
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

    public boolean none() {
        return !iterator().hasNext();
    }

    public boolean none(JFunc.T1<T, Boolean> predicate) {
        for (T item : this) {
            if (predicate.call(item)) return false;
        }
        return true;
    }

    public boolean all(JFunc.T1<T, Boolean> predicate) {
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

    public boolean any(JFunc.T1<T, Boolean> predicate) {
        for (T item : this) {
            if (predicate.call(item)) {
                return true;
            }
        }
        return false;
    }

    public <R> R fold(R initial, JFunc.T2<R, T, R> operation) {
        R accumulator = initial;
        for (T item : this) {
            accumulator = operation.call(accumulator, item);
        }
        return accumulator;
    }

    public <R> R foldIndexed(R initial, JFunc.T3<Integer, R, T, R> operation) {
        int index = 0;
        R accumulator = initial;
        for (T item : this) {
            accumulator = operation.call(index++, accumulator, item);
        }
        return accumulator;
    }

    public static <T extends Comparable<T>> JFunc.T1<JStream<T>, T> max() {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) return null;
            T max = iterator.next();
            while (iterator.hasNext()) {
                T e = iterator.next();
                if (max.compareTo(e) < 0) max = e;
            }
            return max;
        };
    }

    public static <T, R extends Comparable<R>> JFunc.T1<JStream<T>, T> maxBy(JFunc.T1<T, R> selector) {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) return null;
            T maxElem = iterator.next();
            R maxValue = selector.call(maxElem);
            while (iterator.hasNext()) {
                T e = iterator.next();
                R v = selector.call(e);
                if (maxValue.compareTo(v) < 0) {
                    maxElem = e;
                    maxValue = v;
                }
            }
            return maxElem;
        };
    }

    public static <T> JFunc.T1<JStream<T>, T> maxWith(Comparator<? super T> comparator) {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) return null;
            T max = iterator.next();
            while (iterator.hasNext()) {
                T e = iterator.next();
                if (comparator.compare(max, e) < 0) max = e;
            }
            return max;
        };
    }

    public static <T extends Comparable<T>> JFunc.T1<JStream<T>, T> min() {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) return null;
            T min = iterator.next();
            while (iterator.hasNext()) {
                T e = iterator.next();
                if (min.compareTo(e) > 0) min = e;
            }
            return min;
        };
    }


    public static <T, R extends Comparable<R>> JFunc.T1<JStream<T>, T>
    minBy(JFunc.T1<T, R> selector) {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) return null;
            T minElem = iterator.next();
            R minValue = selector.call(minElem);
            while (iterator.hasNext()) {
                T e = iterator.next();
                R v = selector.call(e);
                if (minValue.compareTo(v) > 0) {
                    minElem = e;
                    minValue = v;
                }
            }
            return minElem;
        };
    }

    public static <T> JFunc.T1<JStream<T>, T> minWith(Comparator<? super T> comparator) {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) return null;
            T min = iterator.next();
            while (iterator.hasNext()) {
                T e = iterator.next();
                if (comparator.compare(min, e) > 0) min = e;
            }
            return min;
        };
    }

    public static <S, T extends S> JFunc.T1<JStream<T>, S> reduce(JFunc.T2<S, T, S> operation) {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) {
                throw new UnsupportedOperationException("Empty stream can't be reduced.");
            }
            S accumulator = iterator.next();
            while (iterator.hasNext()) {
                accumulator = operation.call(accumulator, iterator.next());
            }
            return accumulator;
        };
    }

    public static <S, T extends S>
    JFunc.T1<JStream<T>, S> reduceIndexed(JFunc.T3<Integer, S, T, S> operation) {
        return stream -> {
            Iterator<T> iterator = stream.iterator();
            if (!iterator.hasNext()) {
                throw new UnsupportedOperationException("Empty stream can't be reduced.");
            }
            int index = 1;
            S accumulator = iterator.next();
            while (iterator.hasNext()) {
                accumulator = operation.call(index++, accumulator, iterator.next());
            }
            return accumulator;
        };
    }


    public static JFunc.T1<JStream<? extends Number>, Double> average() {
        return stream -> {
            double sum = 0;
            int count = 0;
            for (Number element : stream) {
                sum += element.doubleValue();
                count++;
            }
            return count == 0 ? Double.NaN : sum / count;
        };
    }

    public static JFunc.T1<JStream<? extends Number>, Double> sum() {
        return stream -> {
            double sum = 0;
            for (Number element : stream) {
                sum += element.doubleValue();
            }
            return sum;
        };
    }

    public static <T> JFunc.T1<JStream<T>, Double> sumBy(JFunc.T1<T, Double> selector) {
        return stream -> {
            double sum = 0;
            for (T item : stream) {
                sum += selector.call(item);
            }
            return sum;
        };
    }

    public static <T, A extends Appendable> JFunc.T1<JStream<T>, A> joinTo(
            A buffer, CharSequence separator, int limit, CharSequence truncated,
            JFunc.T1<T, CharSequence> transform) {
        return stream -> {
            try {
                int count = 0;
                for (T element : stream) {
                    if (++count > 1) buffer.append(separator);
                    if (limit < 0 || count <= limit) {
                        if (transform != null) {
                            buffer.append(transform.call(element));
                        } else if (element instanceof CharSequence) {
                            buffer.append((CharSequence) element);
                        } else if (element instanceof Character) {
                            buffer.append((Character) element);
                        } else {
                            buffer.append(element.toString());
                        }
                    } else {
                        break;
                    }
                }
                if (limit >= 0 && count > limit) buffer.append(truncated);
                return buffer;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, A extends Appendable> JFunc.T1<JStream<T>, A> joinTo(
            A buffer, CharSequence separator, int limit, CharSequence truncated) {
        return joinTo(buffer, separator, limit, truncated, null);
    }

    public static <T, A extends Appendable> JFunc.T1<JStream<T>, A> joinTo(
            A buffer, CharSequence separator, int limit) {
        return joinTo(buffer, separator, limit, "...");
    }

    public static <T, A extends Appendable> JFunc.T1<JStream<T>, A> joinTo(
            A buffer, CharSequence separator) {
        return joinTo(buffer, separator, -1);
    }

    public static <T, A extends Appendable>
    JFunc.T1<JStream<T>, A> joinTo(A buffer) {
        return joinTo(buffer, ", ");
    }

    public static <T> String joinToString(
            CharSequence separator, int limit, CharSequence truncated,
            JFunc.T1<T, CharSequence> transform) {
        return joinTo(new StringBuilder(), separator, limit, truncated, transform).toString();
    }

    public static <T> String joinToString(
            CharSequence separator, int limit, CharSequence truncated) {
        return joinToString(separator, limit, truncated, null);
    }

    public static <T> String joinToString(CharSequence separator, int limit) {
        return joinToString(separator, limit, "...");
    }

    public static <T> String joinToString(CharSequence separator) {
        return joinToString(separator, -1);
    }

    public static <T> String joinToString() {
        return joinToString(", ");
    }
}
