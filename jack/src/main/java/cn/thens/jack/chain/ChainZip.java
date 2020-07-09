package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Funcs;

class ChainZip<T, U, R> extends Chain<R> {
    private final Chain<T> first;
    private final Chain<U> second;
    private final Func2.X<? super T, ? super U, ? extends R> zipper;

    ChainZip(Chain<T> first, Chain<U> second, Func2<? super T, ? super U, ? extends R> zipper) {
        this.first = first;
        this.second = second;
        this.zipper = Funcs.of(zipper);
    }

    @Override
    public @NotNull Iterator<R> iterator() {
        Iterator<T> firstIterator = first.iterator();
        Iterator<U> secondIterator = second.iterator();
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return firstIterator.hasNext() && secondIterator.hasNext();
            }

            @Override
            public R next() {
                return zipper.call(firstIterator.next(), secondIterator.next());
            }
        };
    }
}
