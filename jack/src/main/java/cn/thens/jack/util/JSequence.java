package cn.thens.jack.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import cn.thens.jack.func.JAction1;
import cn.thens.jack.func.JAction2;
import cn.thens.jack.func.JFunc0;
import cn.thens.jack.func.JFunc1;
import cn.thens.jack.func.JFunc2;
import cn.thens.jack.func.JFunc3;
import cn.thens.jack.tuple.JTuple;
import cn.thens.jack.tuple.JTuple2;

@SuppressWarnings({"WeakerAccess", "NullableProblems", "unused", "unchecked"})
public abstract class JSequence<T> implements Iterable<T> {
    public static <T> JSequence<T> of(Iterable<T> iterable) {
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }

    @SafeVarargs
    public static <T> JSequence<T> of(T... elements) {
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

    private static JSequence EMPTY = of(EMPTY_ITERATOR);

    public static <T> JSequence<T> empty() {
        return EMPTY;
    }

    public JSequence<T> onEach(JAction1<T> func) {
        return map(it -> {
            func.invoke(it);
            return it;
        });
    }

    public JSequence<T> onEachIndexed(JAction2<Integer, T> func) {
        return mapIndexed((index, value) -> {
            func.invoke(index, value);
            return value;
        });
    }

    public JSequence<T> requireNoNulls() {
        return map(it -> {
            if (it == null) throw new IllegalArgumentException("null element found in " + this);
            return it;
        });
    }

    public <R, V> JSequence<V> zip(JSequence<R> other, JFunc2<T, R, V> transform) {
        JSequence<T> source = this;
        return new JSequence<V>() {
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
                        return transform.invoke(sourceIterator.next(), otherIterator.next());
                    }
                };
            }
        };
    }

    public <R> JSequence<R> zipWithNext(JFunc2<T, T, R> transform) {
        JSequence<T> source = this;
        return new JSequence<R>() {
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
                        return transform.invoke(firstIterator.next(), secondIterator.next());
                    }
                };
            }
        };
    }

    public <R> JSequence<R> map(JFunc1<T, R> transformer) {
        JSequence<T> source = this;
        return new JSequence<R>() {
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
                        return transformer.invoke(iterator.next());
                    }
                };
            }
        };
    }

    public JSequence<JTuple2<Integer, T>> withIndex() {
        JSequence<T> source = this;
        return new JSequence<JTuple2<Integer, T>>() {
            @Override
            public Iterator<JTuple2<Integer, T>> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<JTuple2<Integer, T>>() {
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public JTuple2<Integer, T> next() {
                        return JTuple.of(index++, iterator.next());
                    }
                };
            }
        };
    }

    public <R> JSequence<R> mapIndexed(JFunc2<Integer, T, R> transformer) {
        return withIndex().map(it -> transformer.invoke(it.v1(), it.v2()));
    }

    public <R> JSequence<R> flatten(JFunc1<T, ? extends Iterator<R>> transformer) {
        JSequence<T> source = this;
        return new JSequence<R>() {
            @Override
            public Iterator<R> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<R>() {
                    Iterator<R> itemIterator = null;

                    @Override
                    public boolean hasNext() {
                        return ensureItemIterator();
                    }

                    @Override
                    public R next() {
                        if (!ensureItemIterator()) throw new NoSuchElementException();
                        return itemIterator.next();
                    }

                    boolean ensureItemIterator() {
                        if (itemIterator != null && !itemIterator.hasNext())
                            itemIterator = null;

                        while (itemIterator == null) {
                            if (!iterator.hasNext()) {
                                return false;
                            } else {
                                T element = iterator.next();
                                Iterator<R> nextItemIterator = transformer.invoke(element);
                                if (nextItemIterator.hasNext()) {
                                    itemIterator = nextItemIterator;
                                    return true;
                                }
                            }
                        }
                        return true;
                    }
                };
            }
        };
    }

    public <R> JSequence<R> flatMap(JFunc1<T, ? extends Iterable<R>> transformer) {
        return flatten(it -> transformer.invoke(it).iterator());
    }

    public JSequence<T> sub(int startIndex, int endIndex) {
        JContract.require(startIndex >= 0, "startIndex should be non-negative, but is " + startIndex);
        JContract.require(endIndex >= 0, "endIndex should be non-negative, but is " + endIndex);
        JContract.require(endIndex >= startIndex, "endIndex should be not less than startIndex, but was " + endIndex + " < " + startIndex);
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<T>() {
                    int position = 0;

                    @Override
                    public boolean hasNext() {
                        drop();
                        return (position < endIndex) && iterator.hasNext();
                    }

                    @Override
                    public T next() {
                        drop();
                        if (position >= endIndex) throw new NoSuchElementException();
                        position++;
                        return iterator.next();
                    }

                    void drop() {
                        while (position < startIndex && iterator.hasNext()) {
                            iterator.next();
                            position++;
                        }
                    }
                };
            }
        };
    }

    public JTuple2<List<T>, List<T>> partition(JFunc1<T, Boolean> predicate) {
        List<T> first = new ArrayList<>();
        List<T> second = new ArrayList<>();
        for (T element : this) {
            if (predicate.invoke(element)) {
                first.add(element);
            } else {
                second.add(element);
            }
        }
        return JTuple.of(first, second);
    }

    public JSequence<T> drop(int n) {
        JContract.require(n >= 0, "Requested element count " + n + " is less than zero");
        if (n == 0) return this;
        return sub(n, Integer.MAX_VALUE);
    }

    public JSequence<T> take(int n) {
        JContract.require(n >= 0, "Requested element count " + n + " is less than zero");
        if (n == 0) return empty();
        return sub(0, n);
    }

    public JSequence<T> dropWhile(JFunc1<T, Boolean> predicate) {
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<T>() {
                    int dropState = -1; // -1 for not dropping, 1 for nextItem, 0 for normal iteration
                    T nextItem = null;

                    @Override
                    public boolean hasNext() {
                        if (dropState == -1) drop();
                        return dropState == 1 || iterator.hasNext();
                    }

                    @Override
                    public T next() {
                        if (dropState == -1) drop();

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
                            if (!predicate.invoke(item)) {
                                nextItem = item;
                                dropState = 1;
                                return;
                            }
                        }
                        dropState = 0;
                    }
                };
            }
        };
    }

    public JSequence<T> takeWhile(JFunc1<T, Boolean> predicate) {
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T> iterator = source.iterator();
                return new Iterator<T>() {
                    int nextState = -1; // -1 for unknown, 0 for done, 1 for continue
                    T nextItem = null;

                    @Override
                    public boolean hasNext() {
                        if (nextState == -1)
                            calcNext(); // will change nextState
                        return nextState == 1;
                    }

                    @Override
                    public T next() {
                        if (nextState == -1)
                            calcNext(); // will change nextState
                        if (nextState == 0)
                            throw new NoSuchElementException();
                        T result = nextItem;

                        // Clean next to avoid keeping reference on yielded instance
                        nextItem = null;
                        nextState = -1;
                        return result;
                    }

                    void calcNext() {
                        if (iterator.hasNext()) {
                            T item = iterator.next();
                            if (predicate.invoke(item)) {
                                nextState = 1;
                                nextItem = item;
                                return;
                            }
                        }
                        nextState = 0;
                    }
                };
            }
        };
    }

    public JSequence<T> filter(JFunc1<T, Boolean> predicate) {
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    Iterator<T> iterator = source.iterator();
                    int nextState = -1; // -1 for unknown, 0 for done, 1 for continue
                    T nextItem = null;

                    void calcNext() {
                        while (iterator.hasNext()) {
                            T item = iterator.next();
                            if (predicate.invoke(item)) {
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

    public JSequence<T> filterIndexed(JFunc2<Integer, T, Boolean> predicate) {
        return withIndex()
                .filter(it -> predicate.invoke(it.v1(), it.v2()))
                .map(JTuple2::v2);
    }

    public JSequence<T> filterNot(JFunc1<T, Boolean> predicate) {
        return filter(it -> !predicate.invoke(it));
    }

    public <R> JSequence<R> filterIsInstance(Class<R> clazz) {
        return (JSequence<R>) filter(it -> JAny.of(it).is(clazz));
    }

    public JSequence<T> filterNotNull() {
        //noinspection Convert2MethodRef
        return filterNot(it -> it == null);
    }

    public JSequence<T> sortedWith(Comparator<? super T> comparator) {
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                List<T> sortedList = source.toList();
                Collections.sort(sortedList, comparator);
                return sortedList.iterator();
            }
        };
    }

    public <R extends Comparable<R>> JSequence<T> sortedBy(JFunc1<T, R> selector) {
        return sortedWith(JComparator.by(selector));
    }

    public <R extends Comparable<R>> JSequence<T> sortedByDescending(JFunc1<T, R> selector) {
        return sortedWith((JComparator.by(selector).reversed()));
    }

    public <K> JSequence<T> distinctBy(JFunc1<T, K> keySelector) {
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T> iterator = source.iterator();
                HashSet<K> observed = new HashSet<>();
                return new AbstractIterator<T>() {
                    @Override
                    protected void computeNext() {
                        while (iterator.hasNext()) {
                            T next = iterator.next();
                            K key = keySelector.invoke(next);
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

    public JSequence<T> distinct() {
        return distinctBy(it -> it);
    }

    public JSequence<T> add(Iterable<T> elements) {
        return of(this, of(elements)).flatten(Iterable::iterator);
    }

    public JSequence<T> add(T... elements) {
        return add(of(elements));
    }

    public JSequence<T> remove(Iterable<T> elements) {
        JSequence<T> source = this;
        return new JSequence<T>() {
            @Override
            public Iterator<T> iterator() {
                Set<T> other = of(elements).toSet();
                if (other.isEmpty()) return other.iterator();
                return source.filterNot(other::contains).iterator();
            }
        };
    }

    public JSequence remove(T... element) {
        return remove(of(element));
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

    public <U> JSequence<U> cast(Class<U> clazz) {
        return (JSequence<U>) onEach(item -> {
            if (JAny.of(item).isNot(clazz)) {
                throw new ClassCastException();
            }
        });
    }

    public JSequence<T> ifEmpty(JFunc0<JSequence<T>> defaultValue) {
        return isEmpty() ? defaultValue.invoke() : this;
    }

    public <U> U call(JFunc1<JSequence<T>, U> func) {
        return func.invoke(this);
    }

    public void forEach(JAction1<T> func) {
        for (T item : this) {
            func.invoke(item);
        }
    }

    public void forEachIndexed(JAction2<Integer, T> func) {
        int index = 0;
        for (T item : this) {
            func.invoke(index++, item);
        }
    }

    public boolean isNotEmpty() {
        return iterator().hasNext();
    }

    public boolean isEmpty() {
        return !isNotEmpty();
    }

    public int count() {
        int count = 0;
        for (T item : this) {
            count++;
        }
        return count;
    }

    public int count(JFunc1<T, Boolean> predicate) {
        int count = 0;
        for (T item : this) {
            if (predicate.invoke(item)) {
                count++;
            }
        }
        return count;
    }

    public T getOrElse(int index, JFunc1<Integer, T> defaultValue) {
        if (index < 0) {
            return defaultValue.invoke(index);
        }
        Iterator<T> iterator = iterator();
        int count = 0;
        while (iterator.hasNext()) {
            T element = iterator.next();
            if (index == count++) {
                return element;
            }
        }
        return defaultValue.invoke(index);
    }

    public T getOrNull(int index) {
        return getOrElse(index, it -> null);
    }

    public T get(int index) {
        return getOrElse(index, it -> {
            throw new IndexOutOfBoundsException("Sequence doesn't contain element at index " + index);
        });
    }

    public int indexOf(T element) {
        return firstIndexedOrElse(it -> it.equals(element), () -> null).v1();
    }

    public int indexOf(JFunc1<T, Boolean> predicate) {
        return firstIndexOf(predicate);
    }

    public int firstIndexOf(JFunc1<T, Boolean> predicate) {
        return firstIndexedOrElse(predicate, () -> null).v1();
    }

    public int lastIndexOf(T element) {
        return lastIndexedOrElse(it -> it.equals(element), () -> null).v1();
    }

    public int lastIndexOf(JFunc1<T, Boolean> predicate) {
        return lastIndexedOrElse(predicate, () -> null).v1();
    }

    public boolean contains(T element) {
        return indexOf(element) >= 0;
    }

    public T firstOrElse(JFunc0<T> defaultValue) {
        Iterator<T> iterator = iterator();
        return iterator.hasNext() ? iterator.next() : defaultValue.invoke();
    }

    public T first() {
        return firstOrElse(() -> {
            throw new NoSuchElementException("Sequence is empty.");
        });
    }

    public T firstOrNull() {
        return firstOrElse(() -> null);
    }

    public JTuple2<Integer, T> firstIndexedOrElse(JFunc1<T, Boolean> predicate, JFunc0<T> defaultValue) {
        int index = 0;
        for (T item : this) {
            if (predicate.invoke(item)) {
                return JTuple.of(index, item);
            }
            index++;
        }
        return JTuple.of(-1, defaultValue.invoke());
    }

    public T firstOrElse(JFunc1<T, Boolean> predicate, JFunc0<T> defaultValue) {
        return firstIndexedOrElse(predicate, defaultValue).v2();
    }

    public T first(JFunc1<T, Boolean> predicate) {
        return firstOrElse(predicate, () -> {
            throw new NoSuchElementException("Sequence contains no element matching the predicate.");
        });
    }

    public T firstOrNull(JFunc1<T, Boolean> predicate) {
        return firstOrElse(predicate, () -> null);
    }

    public T lastOrElse(JFunc0<T> defaultValue) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return defaultValue.invoke();
        }
        T last = iterator.next();
        while (iterator.hasNext())
            last = iterator.next();
        return last;
    }

    public T last() {
        return lastOrElse(() -> {
            throw new NoSuchElementException("Sequence is empty.");
        });
    }

    public T lastOrNull() {
        return lastOrElse(() -> null);
    }

    public JTuple2<Integer, T> lastIndexedOrElse(JFunc1<T, Boolean> predicate, JFunc0<T> defaultValue) {
        T last = null;
        int lastIndex = -1;
        int index = 0;
        boolean found = false;
        for (T item : this) {
            if (predicate.invoke(item)) {
                found = true;
                last = item;
                lastIndex = index;
            }
            index++;
        }
        return JTuple.of(lastIndex, found ? last : defaultValue.invoke());
    }

    public T lastOrElse(JFunc1<T, Boolean> predicate, JFunc0<T> defaultValue) {
        return lastIndexedOrElse(predicate, defaultValue).v2();
    }

    public T lastOrNull(JFunc1<T, Boolean> predicate) {
        return lastOrElse(predicate, () -> null);
    }

    public T last(JFunc1<T, Boolean> predicate) {
        return lastOrElse(predicate, () -> {
            throw new NoSuchElementException("Sequence contains no element matching the predicate.");
        });
    }

    public T single() {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new NoSuchElementException("Sequence is empty.");
        }
        T single = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalArgumentException("Sequence has more than one element.");
        }
        return single;
    }

    public T singleOrNull() {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) return null;
        T single = iterator.next();
        if (iterator.hasNext()) return null;
        return single;
    }

    public T single(JFunc1<T, Boolean> predicate) {
        T single = null;
        boolean found = false;
        for (T element : this) {
            if (predicate.invoke(element)) {
                if (found)
                    throw new IllegalArgumentException("Sequence contains more than one matching element.");
                single = element;
                found = true;
            }
        }
        if (!found)
            throw new NoSuchElementException("Sequence contains no element matching the predicate.");
        return single;
    }

    public T singleOrNull(JFunc1<T, Boolean> predicate) {
        T single = null;
        boolean found = false;
        for (T element : this) {
            if (predicate.invoke(element)) {
                if (found) return null;
                single = element;
                found = true;
            }
        }
        if (!found) return null;
        return single;
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

    public <K, V, M extends Map<? super K, ? super V>>
    M associateByTo(M destination, JFunc1<T, K> keySelector, JFunc1<T, V> valueTransform) {
        for (T element : this) {
            destination.put(keySelector.invoke(element), valueTransform.invoke(element));
        }
        return destination;
    }

    public <K, V, M extends Map<? super K, ? super T>>
    M associateByTo(M destination, JFunc1<T, K> keySelector) {
        return associateByTo(destination, keySelector, it -> it);
    }

    public <K, V> Map<K, V> associateBy(JFunc1<T, K> keySelector, JFunc1<T, V> valueTransform) {
        return associateByTo(new LinkedHashMap<>(), keySelector, valueTransform);
    }

    public <K> Map<K, T> associateBy(JFunc1<T, K> keySelector) {
        return associateBy(keySelector, it -> it);
    }

    public <K, V, M extends Map<? super K, ? super V>>
    M associateTo(M destination, JFunc1<T, JTuple2<K, V>> transform) {
        for (T element : this) {
            JTuple2<K, V> pair = transform.invoke(element);
            destination.put(pair.v1(), pair.v2());
        }
        return destination;
    }

    public <K, V> Map<K, V> associate(JFunc1<T, JTuple2<K, V>> transform) {
        return associateTo(new LinkedHashMap<>(), transform);
    }

    public <V, M extends Map<? super T, ? super V>> M associateWithTo(M destination, JFunc1<T, V> valueSelector) {
        return this.associateByTo(destination, it -> it, valueSelector);
    }

    public <K, M extends Map<? super K, List<T>>> M groupByTo(M destination, JFunc1<T, K> keySelector) {
        return groupByTo(destination, keySelector, it -> it);
    }

    public <V> Map<T, V> associateWith(JFunc1<T, V> valueSelector) {
        return associateByTo(new LinkedHashMap<>(), it -> it, valueSelector);
    }

    public <K, V, M extends Map<? super K, List<V>>> M groupByTo(M destination, JFunc1<T, K> keySelector, JFunc1<T, V> valueTransform) {
        for (T element : this) {
            K key = keySelector.invoke(element);
            List<V> list = destination.get(key);
            if (list == null) {
                list = new ArrayList<>();
                destination.put(key, list);
            }
            list.add(valueTransform.invoke(element));
        }
        return destination;
    }

    public <K, V> Map<K, List<V>> groupBy(JFunc1<T, K> keySelector, JFunc1<T, V> valueTransform) {
        return groupByTo(new LinkedHashMap<>(), keySelector, valueTransform);
    }

    public <K> Map<K, List<T>> groupBy(JFunc1<T, K> keySelector) {
        return groupByTo(new LinkedHashMap<>(), keySelector, it -> it);
    }

    public boolean none() {
        return !iterator().hasNext();
    }

    public boolean none(JFunc1<T, Boolean> predicate) {
        for (T item : this) {
            if (predicate.invoke(item)) return false;
        }
        return true;
    }

    public boolean all(JFunc1<T, Boolean> predicate) {
        for (T item : this) {
            if (!predicate.invoke(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean any() {
        return iterator().hasNext();
    }

    public boolean any(JFunc1<T, Boolean> predicate) {
        for (T item : this) {
            if (predicate.invoke(item)) {
                return true;
            }
        }
        return false;
    }

    public <R> R fold(R initial, JFunc2<R, T, R> operation) {
        R accumulator = initial;
        for (T item : this) {
            accumulator = operation.invoke(accumulator, item);
        }
        return accumulator;
    }

    public <R> R foldIndexed(R initial, JFunc3<Integer, R, T, R> operation) {
        int index = 0;
        R accumulator = initial;
        for (T item : this) {
            accumulator = operation.invoke(index++, accumulator, item);
        }
        return accumulator;
    }

    public <R extends Comparable<R>> T maxBy(JFunc1<T, R> selector) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) return null;
        T maxElem = iterator.next();
        R maxValue = selector.invoke(maxElem);
        while (iterator.hasNext()) {
            T e = iterator.next();
            R v = selector.invoke(e);
            if (maxValue.compareTo(v) < 0) {
                maxElem = e;
                maxValue = v;
            }
        }
        return maxElem;
    }

    public T maxWith(Comparator<? super T> comparator) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) return null;
        T max = iterator.next();
        while (iterator.hasNext()) {
            T e = iterator.next();
            if (comparator.compare(max, e) < 0) max = e;
        }
        return max;
    }

    public <R extends Comparable<R>> T minBy(JFunc1<T, R> selector) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) return null;
        T minElem = iterator.next();
        R minValue = selector.invoke(minElem);
        while (iterator.hasNext()) {
            T e = iterator.next();
            R v = selector.invoke(e);
            if (minValue.compareTo(v) > 0) {
                minElem = e;
                minValue = v;
            }
        }
        return minElem;
    }

    public T minWith(Comparator<? super T> comparator) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) return null;
        T min = iterator.next();
        while (iterator.hasNext()) {
            T e = iterator.next();
            if (comparator.compare(min, e) > 0) min = e;
        }
        return min;
    }

    public T reduce(JFunc2<T, T, ? extends T> operation) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new UnsupportedOperationException("Empty sequence can't be reduced.");
        }
        T accumulator = iterator.next();
        while (iterator.hasNext()) {
            accumulator = operation.invoke(accumulator, iterator.next());
        }
        return accumulator;
    }

    public T reduceIndexed(JFunc3<Integer, T, T, ? extends T> operation) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            throw new UnsupportedOperationException("Empty sequence can't be reduced.");
        }
        int index = 1;
        T accumulator = iterator.next();
        while (iterator.hasNext()) {
            accumulator = operation.invoke(index++, accumulator, iterator.next());
        }
        return accumulator;
    }

    public double averageBy(JFunc1<T, ? extends Number> selector) {
        double sum = 0;
        int count = 0;
        for (T element : this) {
            sum += selector.invoke(element).doubleValue();
            count++;
        }
        return count == 0 ? Double.NaN : sum / count;
    }

    public double sumBy(JFunc1<T, ? extends Number> selector) {
        double sum = 0;
        for (T item : this) {
            sum += selector.invoke(item).doubleValue();
        }
        return sum;
    }

    public <A extends Appendable> A joinTo(
            A buffer, CharSequence separator, int limit, CharSequence truncated,
            JFunc1<T, CharSequence> transform) {
        try {
            int count = 0;
            for (T element : this) {
                if (++count > 1) buffer.append(separator);
                if (limit < 0 || count <= limit) {
                    if (transform != null) {
                        buffer.append(transform.invoke(element));
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
    }

    public <A extends Appendable> A joinTo(A buffer, CharSequence separator, int limit, CharSequence truncated) {
        return joinTo(buffer, separator, limit, truncated, null);
    }

    public <A extends Appendable> A joinTo(A buffer, CharSequence separator, int limit) {
        return joinTo(buffer, separator, limit, "...");
    }

    public <A extends Appendable> A joinTo(A buffer, CharSequence separator) {
        return joinTo(buffer, separator, -1);
    }

    public <A extends Appendable> A joinTo(A buffer) {
        return joinTo(buffer, ", ");
    }

    public String joinToString(
            CharSequence separator, int limit, CharSequence truncated,
            JFunc1<T, CharSequence> transform) {
        return joinTo(new StringBuilder(), separator, limit, truncated, transform).toString();
    }

    public String joinToString(CharSequence separator, int limit, CharSequence truncated) {
        return joinToString(separator, limit, truncated, null);
    }

    public String joinToString(CharSequence separator, int limit) {
        return joinToString(separator, limit, "...");
    }

    public String joinToString(CharSequence separator) {
        return joinToString(separator, -1);
    }

    public String joinToString() {
        return joinToString(", ");
    }
}
