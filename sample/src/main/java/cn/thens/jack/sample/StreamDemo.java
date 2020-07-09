package cn.thens.jack.sample;

import cn.thens.jack.func.Funcs;
import cn.thens.jack.ref.Ref;

public class StreamDemo {
    public void test(String text) {
        Ref.of(text).elvisRef("0")
                .safeApply(Integer::parseInt)
                .safeApply(it -> (long) it)
                .safeApply(it -> it + "safeCall")
                .get();
        int i = Funcs.of(() -> 12).invoke();
        Funcs.of(() -> System.out.println("hello, world")).run();
    }
}
