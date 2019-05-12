package cn.thens.jack.sample;

import java.util.List;

import cn.thens.jack.func.JAction;
import cn.thens.jack.func.JFunc;
import cn.thens.jack.util.JAny;
import cn.thens.jack.util.JSequence;

public class StreamDemo {
    public void test(String text) {
        JAny.of(text).elvis("0")
                .call(Integer::parseInt)
                .catchError(it -> (long) it)
                .safeCall(it -> it + "safeCall")
                .get();
        int i = JFunc.call(() -> 12);
        JAction.call(() -> System.out.println("hello, world"));
    }

    public List<Integer> stream() {
        return JSequence.of(1, 2, 3)
                .map(it -> it + 2)
                .toList();
    }
}
