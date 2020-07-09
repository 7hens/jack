package cn.thens.jack.sample;

import cn.thens.jack.func.Funcs;
import cn.thens.jack.ref.Ref;

public class StreamDemo {
    public void test(String text) {
        Ref.of(text).elvisRef("0")
                .safeCall(Integer::parseInt)
                .safeCall(it -> (long) it)
                .safeCall(it -> it + "safeCall")
                .get();
        int i = Funcs.of(() -> 12).call();
        Funcs.of(() -> System.out.println("hello, world")).run();
    }
}
