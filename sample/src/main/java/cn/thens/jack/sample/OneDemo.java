package cn.thens.jack.sample;

import java.util.List;

import cn.thens.jack.func.JAction;
import cn.thens.jack.func.JFunc;
import cn.thens.jack.stream.JAny;
import cn.thens.jack.stream.JStream;

public class OneDemo {
    public void test(String text) {
        JAny.of(text).elvis("0")
                .let(Integer::parseInt)
                .cast(long.class)
                .catchError(it -> it.cast(Long.class))
                .safeCall(it -> JAny.empty())
                .get();
        int i = JFunc.call(() -> 12);
        JAction.call(() -> System.out.println("hello, world"));
    }

    public List<Integer> stream() {
        return JStream.of(1, 2, 3)
                .map(it -> it + 2)
                .toList();
    }
}
