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
@SuppressWarnings("NullableProblems")
public class ChainMap<T, R> extends Chain<R> {
    private final Chain<T> upChain;
    private final Func1.X<? super T, ? extends R> mapper;

    private ChainMap(Chain<T> upChain, Func1<? super T, ? extends R> mapper) {
        this.upChain = upChain;
        this.mapper = Functions.of(mapper);
    }

    @NotNull
    @Override
    public Iterator<R> iterator() {
        Iterator<T> iterator = upChain.iterator();
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

    static <T, R> Chain<R> map(Chain<T> upChain, Func1<? super T, ? extends R> mapper) {
        return new ChainMap<>(upChain, mapper);
    }

    static <T, R> Chain<R> mapIndexed(Chain<T> upChain, Func2<? super Integer, ? super T, ? extends R> mapper) {
        AtomicInteger index = new AtomicInteger(0);
        return map(upChain, item -> mapper.invoke(index.getAndIncrement(), item));
    }

    static <T> Chain<Tuple2<Integer, T>> withIndex(Chain<T> upChain) {
        return mapIndexed(upChain, Tuples::of);
    }

    static <T> Chain<T> onEach(Chain<T> upChain, Action1<? super T> action) {
        return map(upChain, item -> {
            action.run(item);
            return item;
        });
    }

    static <T> Chain<T> onEachIndexed(Chain<T> upChain, Action2<? super Integer, ? super T> action) {
        return mapIndexed(upChain, (index, item) -> {
            action.run(index, item);
            return item;
        });
    }

    static <T> Chain<T> requireNoNulls(Chain<T> upChain) {
        return map(upChain, item -> {
            if (item == null) {
                throw new IllegalArgumentException("null element found in " + upChain);
            }
            return item;
        });
    }

    static <T, U> Chain<U> cast(Chain<T> upChain, Class<U> clazz) {
        return map(upChain, item -> Ref.of(item).cast(clazz).get());
    }
}
