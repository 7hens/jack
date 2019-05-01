package cn.thens.jack;

import java.util.ArrayList;
import java.util.List;

public final class JOneList {
    private JOneList() {
    }

    public static <T> JFunc.A1<List<T>> each(final JFunc.A1<T> iterator) {
        return list -> {
            for (T t : list) {
                iterator.run(t);
            }
        };
    }

    public static <T, U> JFunc.F1<List<T>, List<U>> map(JFunc.F1<T, U> mapper) {
        return list -> {
            List<U> mappedList = new ArrayList<>();
            for (T t : list) {
                mappedList.add(mapper.call(t));
            }
            return mappedList;
        };
    }
}
