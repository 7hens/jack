package cn.thens.jack.chain;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.thens.jack.func.Comparator;

class ChainSort<T> extends Chain<T> {
    private final Chain<T> up;
    Comparator.X<? super T> comparatorX;

    ChainSort(Chain<T> up, Comparator<? super T> comparator) {
        this.up = up;
        comparatorX = Comparator.X.of(comparator);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        List<T> sortedList = up.toList();
        Collections.sort(sortedList, comparatorX::compare);
        return sortedList.iterator();
    }
}
