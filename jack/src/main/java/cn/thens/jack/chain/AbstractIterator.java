package cn.thens.jack.chain;

import java.util.Iterator;
import java.util.NoSuchElementException;

import cn.thens.jack.func.Exceptions;

/**
 * @author 7hens
 */
abstract class AbstractIterator<T> implements Iterator<T> {
    private enum State {READY, NOT_READY, DONE, FAILED}

    private State state = State.NOT_READY;
    private T nextValue;

    protected abstract void computeNext() throws Throwable;

    @Override
    public boolean hasNext() {
        if (state == State.FAILED) {
            throw new AssertionError();
        }
        try {
            switch (state) {
                case DONE:
                    return false;
                case READY:
                    return true;
                default:
                    return tryToComputeNext();
            }
        } catch (Throwable e) {
            throw Exceptions.runtime(e);
        }
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        state = State.NOT_READY;
        return nextValue;
    }

    private boolean tryToComputeNext() throws Throwable {
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
