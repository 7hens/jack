package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Functions;
import cn.thens.jack.ref.Ref;
import cn.thens.jack.tuple.Tuple2;
import cn.thens.jack.tuple.Tuples;

/**
 * @author 7hens
 */
class ChainMap<T, R> extends Chain<R> {
    private final Chain<T> up;
    private final Func1.X<? super T, ? extends R> mapper;

    private ChainMap(Chain<T> up, Func1<? super T, ? extends R> mapper) {
        this.up = up;
        this.mapper = Functions.of(mapper);
    }

    @NotNull
    @Override
    public Iterator<R> iterator() {
        Iterator<T> iterator = up.iterator();
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return mapper.invoke(iterator.next());
            }
        };
    }

    static <T, R> Chain<R> map(Chain<T> up, Func1<? super T, ? extends R> mapper) {
        return new ChainMap<>(up, mapper);
    }

    static <T, R> Chain<R> mapIndexed(Chain<T> up, Func2<? super Integer, ? super T, ? extends R> mapper) {
        AtomicInteger index = new AtomicInteger(0);
        return map(up, item -> mapper.invoke(index.getAndIncrement(), item));
    }

    static <T> Chain<Tuple2<Integer, T>> withIndex(Chain<T> up) {
        return mapIndexed(up, Tuples::of);
    }

    static <T> Chain<T> onEach(Chain<T> up, Action1<? super T> action) {
        return map(up, item -> {
            action.run(item);
            return item;
        });
    }

    static <T> Chain<T> onEachIndexed(Chain<T> up, Action2<? super Integer, ? super T> action) {
        return mapIndexed(up, (index, item) -> {
            action.run(index, item);
            return item;
        });
    }

    static <T> Chain<T> requireNoNulls(Chain<T> up) {
        return map(up, item -> {
            if (item == null) {
                throw new IllegalArgumentException("null element found in " + up);
            }
            return item;
        });
    }

    static <T, U> Chain<U> cast(Chain<T> up, Class<U> clazz) {
        return map(up, item -> Ref.of(item).cast(clazz).get());
    }
}
