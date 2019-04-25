package cn.thens.jack.sample;

import cn.thens.jack.JOne;

public class OneDemo {
    public void test(String text) {
        JOne.of(text).elvis("0")
                .safe()
                .map(Integer::parseInt)
                .cast(long.class)
                .get();
        int i = JOne.run(() -> 12);
        JOne.run(() -> System.out.println("hello, world"));
    }
}
