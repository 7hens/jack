package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author 7hens
 */
@SuppressWarnings({"NullableProblems", "unchecked"})
class ChainCreate {
    static <T> Chain<T> from(final Iterable<T> iterable) {
        return new Chain<T>() {
            @NotNull
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }

    static <T> Chain<T> from(final T[] elements) {
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return from(list);
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

    private static Chain EMPTY = new Chain() {
        @NotNull
        @Override
        public Iterator iterator() {
            return EMPTY_ITERATOR;
        }
    };

    public static <T> Chain<T> empty() {
        return EMPTY;
    }
}
