package cn.thens.jack.sample;

import java.util.ArrayList;
import java.util.List;

import cn.thens.jack.JFunc;
import cn.thens.jack.JOne;
import cn.thens.jack.JOneList;

public class OneDemo {
    public void test(String text) {
        JOne.of(text).elvis("0")
                .map(Integer::parseInt)
                .cast(long.class)
                .catchError(it -> it.cast(Long.class))
                .safeCall(it -> JOne.empty())
                .get();
        int i = JFunc.call(() -> 12);
        JFunc.run(() -> System.out.println("hello, world"));

        JOne.of(new ArrayList<Integer>())
                .also(JOneList.each(System.out::println))
                .map(JOneList.map(Object::toString))
                .get();
    }

    private static <T, U> JFunc.F1<List<T>, List<U>> map(JFunc.F1<T, U> mapper) {
        return list -> {
            List<U> mappedList = new ArrayList<>();
            for (T t : list) {
                mappedList.add(mapper.call(t));
            }
            return mappedList;
        };
    }

}
