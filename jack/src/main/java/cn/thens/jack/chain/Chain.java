package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import cn.thens.jack.flow.Flow;
import cn.thens.jack.flow.IFlow;
import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Comparator;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Func3;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.func.Predicate;
import cn.thens.jack.func.Things;
import cn.thens.jack.ref.IRef;
import cn.thens.jack.ref.Ref;
import cn.thens.jack.tuple.Tuple2;
import cn.thens.jack.tuple.Tuples;

/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public abstract class Chain<T> implements Iterable<T>, IChain<T>, IFlow<T>, IRef<Chain<T>> {
    @NotNull
    public abstract Iterator<T> iterator();

    @Override
    public Chain<T> asChain() {
        return this;
    }

    @Override
    public Flow<T> asFlow() {
        return Flow.from(this);
    }

    @Override
    public Ref<Chain<T>> asRef() throws Throwable {
        return Ref.of(this);
    }

    public <R> R to(Func1<? super Chain<T>, ? extends R> converter) {
        return Funcs.of(converter).call(this);
    }

    public <R> Chain<R> map(Func1<? super T, ? extends R> transformer) {
        return ChainMap.map(this, transformer);
    }

    public <R> Chain<R> mapIndexed(Func2<Integer, T, R> mapper) {
        return ChainMap.mapIndexed(this, mapper);
    }

    public Chain<Tuple2<Integer, T>> withIndex() {
        return ChainMap.withIndex(this);
    }

    public Chain<T> onEach(Action1<? super T> action) {
        return ChainMap.onEach(this, action);
    }

    public Chain<T> onEachIndexed(Action2<? super Integer, ? super T> action) {
        return ChainMap.onEachIndexed(this, action);
    }

    public Chain<T> requireNoNulls() {
        return ChainMap.requireNoNulls(this);
    }

    public <U> Chain<U> cast(Class<U> clazz) {
        return ChainMap.cast(this, clazz);
    }

    public <U, R> Chain<R> zip(Chain<U> other, Func2<T, U, R> zipper) {
        return new ChainZip<>(this, other, zipper);
    }

    public <R> Chain<R> flatMap(Func1<T, ? extends Iterable<R>> transformer) {
        return flatten(it -> transformer.call(it).iterator());
    }

    public Chain<T> sub(int startIndex, int endIndex) {
        Things.require(startIndex >= 0,
                "startIndex should be non-negative, but is " + startIndex);
        Things.require(endIndex >= 0, "endIndex should be non-negative, but is " + endIndex);
        Things.require(endIndex >= startIndex,
                "endIndex should be not less than startIndex, but was " + endIndex + " < " +
                        startIndex);
        Chain<T> source = this;
        return new Chain<T>() {
            @NotNull
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
                        if (position >= endIndex) {
                            throw new NoSuchElementException();
                        }
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

    public Tuple2<Chain<T>, Chain<T>> partition(Predicate<? super T> predicate) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        List<T> first = new ArrayList<>();
        List<T> second = new ArrayList<>();
        for (T element : this) {
            if (predicateX.test(element)) {
                first.add(element);
            } else {
                second.add(element);
            }
        }
        return Tuples.of(of(first), of(second));
    }

    public Chain<T> takeUtil(Predicate<T> predicate) {
        return new ChainTakeUtil<>(this, predicate);
    }

    public Chain<T> take(int n) {
        Things.require(n > 0, "Requested element count " + n + " is less than zero");
        if (n == 0) return empty();
        return takeUtil(Predicate.X.skip(n - 1));
    }

    public Chain<T> takeWhile(Predicate<? super T> predicate) {
        return new ChainTakeWhile<>(this, predicate);
    }

    public Chain<T> skipWhile(Predicate<T> predicate) {
        return new ChainSkipWhile<>(this, predicate);
    }

    public Chain<T> skip(int n) {
        Things.require(n >= 0, "Requested element count " + n + " is less than zero");
        return filter(Predicate.X.skip(n));
    }

    public Chain<T> filter(Predicate<T> predicate) {
        return ChainFilter.filter(this, predicate);
    }

    public Chain<T> filterNot(Predicate<T> predicate) {
        return filter(Predicate.X.of(predicate).not());
    }

    public <R> Chain<R> filterIsInstance(Class<R> clazz) {
        return filter(it -> Ref.of(it).is(clazz)).cast(clazz);
    }

    public Chain<T> filterNotNull() {
        //noinspection Convert2MethodRef
        return filter(it -> it != null);
    }

    public Chain<T> sortedWith(Comparator<? super T> comparator) {
        return new ChainSort<>(this, comparator);
    }

    public <R extends Comparable<R>> Chain<T> sortedBy(Func1<? super T, ? extends R> selector) {
        return sortedWith(Comparator.X.by(selector));
    }

    public <R extends Comparable<R>> Chain<T> sortedByDescending(Func1<? super T, ? extends R> selector) {
        return sortedWith((Comparator.X.by(selector).reversed()));
    }

    public <K> Chain<T> distinctBy(Func1<? super T, ? extends K> keySelector) {
        return filter(Predicate.X.distinctBy(keySelector));
    }

    public Chain<T> distinct() {
        return filter(Predicate.X.distinct());
    }

    public Chain<T> add(Iterable<T> elements) {
        return of(this, of(elements)).flatten(Iterable::iterator);
    }

    public Chain<T> add(T... elements) {
        return add(of(elements));
    }

    public Chain<T> remove(Iterable<T> elements) {
        Chain<T> source = this;
        return new Chain<T>() {
            @NotNull
            @Override
            public Iterator<T> iterator() {
                Set<T> other = of(elements).toSet();
                if (other.isEmpty()) {
                    return other.iterator();
                }
                return source.filterNot(other::contains).iterator();
            }
        };
    }

    @SafeVarargs
    public final Chain<T> remove(T... element) {
        return remove(of(element));
    }

    public Chain<T> ifEmpty(Func0<? extends Chain<T>> defaultValue) {
        return isEmpty() ? Func0.X.of(defaultValue).call() : this;
    }

    public <U> U call(Func1<Chain<T>, U> func) {
        return Func1.X.of(func).call(this);
    }

    public void forEach(Action1<? super T> func) {
        Action1.X<? super T> action = Action1.X.of(func);
        for (T item : this) {
            action.run(item);
        }
    }

    public void forEachIndexed(Action2<? super Integer, ? super T> func) {
        int index = 0;
        Action2.X<? super Integer, ? super T> action = Action2.X.of(func);
        for (T item : this) {
            action.run(index++, item);
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

    public int count(Predicate<? super T> predicate) {
        int count = 0;
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        for (T item : this) {
            if (predicateX.test(item)) {
                count++;
            }
        }
        return count;
    }

    public T getOrElse(int index, Func1<? super Integer, ? extends T> defaultValue) {
        Func1.X<? super Integer, ? extends T> funcX = Funcs.of(defaultValue);
        if (index < 0) {
            return funcX.call(index);
        }
        Iterator<T> iterator = iterator();
        int count = 0;
        while (iterator.hasNext()) {
            T element = iterator.next();
            if (index == count++) {
                return element;
            }
        }
        return funcX.call(index);
    }

    public T getOrNull(int index) {
        return getOrElse(index, it -> null);
    }

    public T get(int index) {
        return getOrElse(index, it -> {
            throw new IndexOutOfBoundsException(
                    "Sequence doesn't contain element at index " + index);
        });
    }

    public int indexOf(T element) {
        return firstIndexedOrElse(it -> it.equals(element), () -> null).v1();
    }

    public int indexOf(Predicate<? super T> predicate) {
        return firstIndexOf(predicate);
    }

    public int firstIndexOf(Predicate<? super T> predicate) {
        return firstIndexedOrElse(predicate, () -> null).v1();
    }

    public int lastIndexOf(T element) {
        return lastIndexedOrElse(it -> it.equals(element), () -> null).v1();
    }

    public int lastIndexOf(Predicate<? super T> predicate) {
        return lastIndexedOrElse(predicate, () -> null).v1();
    }

    public boolean contains(T element) {
        return indexOf(element) >= 0;
    }

    public T firstOrElse(Func0<T> defaultValue) {
        Iterator<T> iterator = iterator();
        return iterator.hasNext() ? iterator.next() : Funcs.of(defaultValue).call();
    }

    public T first() {
        return firstOrElse(() -> {
            throw new NoSuchElementException("Sequence is empty.");
        });
    }

    public T firstOrNull() {
        return firstOrElse(() -> null);
    }

    public Tuple2<Integer, T> firstIndexedOrElse(Predicate<? super T> predicate,
                                                 Func0<T> defaultValue) {
        int index = 0;
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        for (T item : this) {
            if (predicateX.test(item)) {
                return Tuples.of(index, item);
            }
            index++;
        }
        return Tuples.of(-1, Funcs.of(defaultValue).call());
    }

    public T firstOrElse(Predicate<? super T> predicate, Func0<T> defaultValue) {
        return firstIndexedOrElse(predicate, defaultValue).v2();
    }

    public T first(Predicate<? super T> predicate) {
        return firstOrElse(predicate, () -> {
            throw new NoSuchElementException(
                    "Sequence contains no element matching the predicate.");
        });
    }

    public T firstOrNull(Predicate<? super T> predicate) {
        return firstOrElse(predicate, () -> null);
    }

    public T lastOrElse(Func0<T> defaultValue) {
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return Funcs.of(defaultValue).call();
        }
        T last = iterator.next();
        while (iterator.hasNext()) {
            last = iterator.next();
        }
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

    public Tuple2<Integer, T> lastIndexedOrElse(Predicate<? super T> predicate,
                                                Func0<T> defaultValue) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        T last = null;
        int lastIndex = -1;
        int index = 0;
        boolean found = false;
        for (T item : this) {
            if (predicateX.test(item)) {
                found = true;
                last = item;
                lastIndex = index;
            }
            index++;
        }
        return Tuples.of(lastIndex, found ? last : Funcs.of(defaultValue).call());
    }

    public T lastOrElse(Predicate<? super T> predicate, Func0<T> defaultValue) {
        return lastIndexedOrElse(predicate, defaultValue).v2();
    }

    public T lastOrNull(Predicate<? super T> predicate) {
        return lastOrElse(predicate, () -> null);
    }

    public T last(Predicate<? super T> predicate) {
        return lastOrElse(predicate, () -> {
            throw new NoSuchElementException(
                    "Sequence contains no element matching the predicate.");
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
        if (!iterator.hasNext()) {
            return null;
        }
        T single = iterator.next();
        if (iterator.hasNext()) {
            return null;
        }
        return single;
    }

    public T single(Predicate<? super T> predicate) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        T single = null;
        boolean found = false;
        for (T element : this) {
            if (predicateX.test(element)) {
                if (found) {
                    throw new IllegalArgumentException(
                            "Sequence contains more than one matching element.");
                }
                single = element;
                found = true;
            }
        }
        if (!found) {
            throw new NoSuchElementException(
                    "Sequence contains no element matching the predicate.");
        }
        return single;
    }

    public T singleOrNull(Predicate<? super T> predicate) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        T single = null;
        boolean found = false;
        for (T element : this) {
            if (predicateX.test(element)) {
                if (found) {
                    return null;
                }
                single = element;
                found = true;
            }
        }
        if (!found) {
            return null;
        }
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

    public T[] toArray(T[] target) {
        int count = 0;
        Object[] container = new Object[8];
        for (T element : this) {
            if (count == container.length) {
                Object[] temp = new Object[count + Math.max(count >> 2, 8)];
                System.arraycopy(container, 0, temp, 0, count);
                container = temp;
            }
            container[count++] = element;
        }

        if (target.length < count) {
            return (T[]) Arrays.copyOf(container, count, target.getClass());
        } else {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(container, 0, target, 0, count);
            if (target.length > count) {
                target[count] = null;
            }

            return target;
        }
    }

    public <K, V, M extends Map<? super K, ? super V>>
    M associateByTo(M destination, Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueTransform) {
        Func1.X<T, K> keySelectorX = Funcs.of(keySelector);
        Func1.X<T, V> valueTransformX = Funcs.of(valueTransform);
        for (T element : this) {
            destination.put(keySelectorX.call(element), valueTransformX.call(element));
        }
        return destination;
    }

    public <K, V, M extends Map<? super K, ? super T>>
    M associateByTo(M destination, Func1<T, K> keySelector) {
        return associateByTo(destination, keySelector, it -> it);
    }

    public <K, V> Map<K, V> associateBy(Func1<T, K> keySelector, Func1<T, V> valueTransform) {
        return associateByTo(new LinkedHashMap<>(), keySelector, valueTransform);
    }

    public <K> Map<K, T> associateBy(Func1<T, K> keySelector) {
        return associateBy(keySelector, it -> it);
    }

    public <K, V, M extends Map<? super K, ? super V>>
    M associateTo(M destination, Func1<? super T, ? extends Tuple2<? extends K, ? extends V>> transform) {
        Func1.X<? super T, ? extends Tuple2<? extends K, ? extends V>> transformX = Funcs.of(transform);
        for (T element : this) {
            Tuple2<? extends K, ? extends V> pair = transformX.call(element);
            destination.put(pair.v1(), pair.v2());
        }
        return destination;
    }

    public <K, V> Map<K, V> associate(Func1<? super T, ? extends Tuple2<? extends K, ? extends V>> transform) {
        return associateTo(new LinkedHashMap<>(), transform);
    }

    public <V, M extends Map<? super T, ? super V>> M associateWithTo(
            M destination, Func1<? super T, ? extends V> valueSelector) {
        return associateByTo(destination, Funcs.self(), valueSelector);
    }

    public <K, M extends Map<? super K, List<T>>> M groupByTo(
            M destination, Func1<? super T, ? extends K> keySelector) {
        return groupByTo(destination, keySelector, Funcs.self());
    }

    public <V> Map<T, V> associateWith(Func1<? super T, ? extends V> valueSelector) {
        return associateByTo(new LinkedHashMap<>(), Funcs.self(), valueSelector);
    }

    public <K, V, M extends Map<? super K, List<V>>> M groupByTo(
            M destination, Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueTransform) {
        Func1.X<? super T, ? extends K> keySelectorX = Funcs.of(keySelector);
        Func1.X<? super T, ? extends V> valueTransformX = Funcs.of(valueTransform);
        for (T element : this) {
            K key = keySelectorX.call(element);
            List<V> list = destination.get(key);
            if (list == null) {
                list = new ArrayList<>();
                destination.put(key, list);
            }
            list.add(valueTransformX.call(element));
        }
        return destination;
    }

    public <K, V> Map<K, List<V>> groupBy(Func1<T, K> keySelector, Func1<T, V> valueTransform) {
        return groupByTo(new LinkedHashMap<>(), keySelector, valueTransform);
    }

    public <K> Map<K, List<T>> groupBy(Func1<T, K> keySelector) {
        return groupByTo(new LinkedHashMap<>(), keySelector, it -> it);
    }

    public boolean none() {
        return !iterator().hasNext();
    }

    public boolean none(Predicate<? super T> predicate) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        for (T item : this) {
            if (predicateX.test(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean all(Predicate<? super T> predicate) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        for (T item : this) {
            if (!predicateX.test(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean any() {
        return iterator().hasNext();
    }

    public boolean any(Predicate<? super T> predicate) {
        Predicate.X<? super T> predicateX = Predicate.X.of(predicate);
        for (T item : this) {
            if (predicateX.test(item)) {
                return true;
            }
        }
        return false;
    }

    public <R> R fold(R initial, Func2<? super R, ? super T, ? extends R> operation) {
        R accumulator = initial;
        Func2.X<? super R, ? super T, ? extends R> operationX = Funcs.of(operation);
        for (T item : this) {
            accumulator = operationX.call(accumulator, item);
        }
        return accumulator;
    }

    public <R> R foldIndexed(R initial, Func3<? super Integer, ? super R, ? super T, ? extends R> operation) {
        Func3.X<? super Integer, ? super R, ? super T, ? extends R> operationX = Funcs.of(operation);
        int index = 0;
        R accumulator = initial;
        for (T item : this) {
            accumulator = operationX.call(index++, accumulator, item);
        }
        return accumulator;
    }

    public <R extends Comparable<R>> T maxBy(Func1<? super T, ? extends R> selector) {
        Func1.X<? super T, ? extends R> selectorX = Funcs.of(selector);
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T maxElem = iterator.next();
        R maxValue = selectorX.call(maxElem);
        while (iterator.hasNext()) {
            T e = iterator.next();
            R v = selectorX.call(e);
            if (maxValue.compareTo(v) < 0) {
                maxElem = e;
                maxValue = v;
            }
        }
        return maxElem;
    }

    public T maxWith(Comparator<? super T> comparator) {
        Iterator<T> iterator = iterator();
        Comparator.X<? super T> comparatorX = Comparator.X.of(comparator);
        if (!iterator.hasNext()) {
            return null;
        }
        T max = iterator.next();
        while (iterator.hasNext()) {
            T e = iterator.next();
            if (comparatorX.compare(max, e) < 0) {
                max = e;
            }
        }
        return max;
    }

    public <R extends Comparable<R>> T minBy(Func1<? super T, ? extends R> selector) {
        Func1.X<? super T, ? extends R> selectorX = Funcs.of(selector);
        Iterator<T> iterator = iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        T minElem = iterator.next();
        R minValue = selectorX.call(minElem);
        while (iterator.hasNext()) {
            T e = iterator.next();
            R v = selectorX.call(e);
            if (minValue.compareTo(v) > 0) {
                minElem = e;
                minValue = v;
            }
        }
        return minElem;
    }

    public T minWith(Comparator<? super T> comparator) {
        Iterator<T> iterator = iterator();
        Comparator.X<? super T> comparatorX = Comparator.X.of(comparator);
        if (!iterator.hasNext()) {
            return null;
        }
        T min = iterator.next();
        while (iterator.hasNext()) {
            T e = iterator.next();
            if (comparatorX.compare(min, e) > 0) {
                min = e;
            }
        }
        return min;
    }

    public T reduce(Func2<? super T, ? super T, ? extends T> operation) {
        Iterator<T> iterator = iterator();
        Func2.X<? super T, ? super T, ? extends T> operationX = Funcs.of(operation);
        if (!iterator.hasNext()) {
            throw new UnsupportedOperationException("Empty sequence can't be reduced.");
        }
        T accumulator = iterator.next();
        while (iterator.hasNext()) {
            accumulator = operationX.call(accumulator, iterator.next());
        }
        return accumulator;
    }

    public T reduceIndexed(Func3<? super Integer, ? super T, ? super T, ? extends T> operation) {
        Iterator<T> iterator = iterator();
        Func3.X<? super Integer, ? super T, ? super T, ? extends T> operationX = Funcs.of(operation);
        if (!iterator.hasNext()) {
            throw new UnsupportedOperationException("Empty sequence can't be reduced.");
        }
        int index = 1;
        T accumulator = iterator.next();
        while (iterator.hasNext()) {
            accumulator = operationX.call(index++, accumulator, iterator.next());
        }
        return accumulator;
    }

    public double averageBy(Func1<? super T, ? extends Number> selector) {
        Func1.X<? super T, ? extends Number> selectorX = Funcs.of(selector);
        double sum = 0;
        int count = 0;
        for (T element : this) {
            sum += selectorX.call(element).doubleValue();
            count++;
        }
        return count == 0 ? Double.NaN : sum / count;
    }

    public double sumBy(Func1<? super T, ? extends Number> selector) {
        Func1.X<? super T, ? extends Number> selectorX = Funcs.of(selector);
        double sum = 0;
        for (T item : this) {
            sum += selectorX.call(item).doubleValue();
        }
        return sum;
    }

    public <R> Chain<R> zipWithNext(Func2<? super T, ? super T, ? extends R> transform) {
        Func2.X<? super T, ? super T, ? extends R> transformX = Funcs.of(transform);
        Chain<T> source = this;
        return new Chain<R>() {
            @NotNull
            @Override
            public Iterator<R> iterator() {
                Iterator<T> firstIterator = source.iterator();
                Iterator<T> secondIterator = source.iterator();
                if (secondIterator.hasNext()) {
                    secondIterator.next();
                }
                if (!secondIterator.hasNext()) {
                    return Chain.<R>empty().iterator();
                }
                return new Iterator<R>() {
                    @Override
                    public boolean hasNext() {
                        return secondIterator.hasNext();
                    }

                    @Override
                    public R next() {
                        return transformX.call(firstIterator.next(), secondIterator.next());
                    }
                };
            }
        };
    }

    public <R> Chain<R> flatten(Func1<? super T, ? extends Iterator<R>> transformer) {
        Chain<T> source = this;
        Func1.X<? super T, ? extends Iterator<R>> transformerX = Funcs.of(transformer);
        return new Chain<R>() {
            @Override
            @NotNull
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
                        if (!ensureItemIterator()) {
                            throw new NoSuchElementException();
                        }
                        return itemIterator.next();
                    }

                    boolean ensureItemIterator() {
                        if (itemIterator != null && !itemIterator.hasNext()) {
                            itemIterator = null;
                        }

                        while (itemIterator == null) {
                            if (!iterator.hasNext()) {
                                return false;
                            } else {
                                T element = iterator.next();
                                Iterator<R> nextItemIterator = transformerX.call(element);
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

    public <A extends Appendable> A joinTo(
            A buffer, CharSequence separator, int limit, CharSequence truncated,
            Func1<? super T, CharSequence> transform) {
        try {
            int count = 0;
            for (T element : this) {
                if (++count > 1) {
                    buffer.append(separator);
                }
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
            if (limit >= 0 && count > limit) {
                buffer.append(truncated);
            }
            return buffer;
        } catch (Throwable e) {
            throw Things.wrap(e);
        }
    }

    public <A extends Appendable> A joinTo(A buffer, CharSequence separator, int limit,
                                           CharSequence truncated) {
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
            Func1<T, CharSequence> transform) {
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

    public static <T> Chain<T> of(Iterable<T> iterable) {
        return ChainCreate.from(iterable);
    }

    @SafeVarargs
    public static <T> Chain<T> of(T... elements) {
        return ChainCreate.from(elements);
    }

    public static <T> Chain<T> empty() {
        return ChainCreate.empty();
    }

}
