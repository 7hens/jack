package cn.thens.jack.sample;

import java.util.List;

import cn.thens.jack.JFunc;
import cn.thens.jack.JOne;
import cn.thens.jack.JStream;

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

    public List<Integer> stream() {
        return JStream.of(1, 2, 3)
                .map(it -> it + 2)
                .toList();
    }
}
