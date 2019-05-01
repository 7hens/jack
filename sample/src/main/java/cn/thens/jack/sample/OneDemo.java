package cn.thens.jack.sample;

import cn.thens.jack.JFunc;
import cn.thens.jack.JList;
import cn.thens.jack.JOne;

public class OneDemo {
    public void test(String text) {
        JOne.of(text).elvis("0")
                .let(Integer::parseInt)
                .cast(long.class)
                .catchError(it -> it.cast(Long.class))
                .safeCall(it -> JOne.empty())
                .get();
        int i = JFunc.call(() -> 12);
        JFunc.run(() -> System.out.println("hello, world"));

    }

    public void list() {
        JList.of(1, 2, 3)
                .also(JList.each(System.out::println))
                .eval(JList.map(it -> it + 2))
                .get();
    }
}
