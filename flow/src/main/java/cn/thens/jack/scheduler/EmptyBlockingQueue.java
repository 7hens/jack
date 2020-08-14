package cn.thens.jack.scheduler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
final class EmptyBlockingQueue<E> implements BlockingQueue<E> {
    @Override
    public boolean add(@NotNull E e) {
        return false;
    }

    @Override
    public boolean offer(@NotNull E e) {
        return false;
    }

    @Override
    public E remove() {
        return null;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public void put(@NotNull E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long l, @NotNull TimeUnit timeUnit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public E take() throws InterruptedException {
        throw new InterruptedException();
    }

    @Nullable
    @Override
    public E poll(long l, @NotNull TimeUnit timeUnit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public E next() {
                return null;
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        return ts;
    }

    @Override
    public int drainTo(@NotNull Collection<? super E> collection) {
        return 0;
    }

    @Override
    public int drainTo(@NotNull Collection<? super E> collection, int i) {
        return 0;
    }
}
