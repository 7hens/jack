package cn.thens.jack.sample;

import cn.thens.jack.JFunc;
import cn.thens.jack.JOne;

public class OneDemo {
    public void test(String text) {
        JOne.of(text).elvis("0")
                .map(Integer::parseInt)
                .as(long.class)
                .get();
        int i = JFunc.run(() -> 12);
        JFunc.run(() -> System.out.println("hello, world"));
    }
}
