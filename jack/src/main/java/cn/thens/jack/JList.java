package cn.thens.jack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JList {
    private JList() {
    }

    public static <T> JOne<List<T>> of(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return JOne.of(list);
    }

    public static <T> JOne<List<T>> empty() {
        return JOne.of(new ArrayList<>());
    }

    public static <T> JFunc.A1<List<T>> each(final JFunc.A1<T> iterator) {
        return list -> {
            for (T t : list) {
                iterator.run(t);
            }
        };
    }

    public static <T, U> JFunc.F1<JOne<List<T>>, JOne<List<U>>> map(JFunc.F1<T, U> mapper) {
        return one -> {
            List<U> mappedList = new ArrayList<>();
            for (T t : one.get()) {
                mappedList.add(mapper.call(t));
            }
            return JOne.of(mappedList);
        };
    }
}
