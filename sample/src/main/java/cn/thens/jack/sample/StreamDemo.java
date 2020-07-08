package cn.thens.jack.sample;

import cn.thens.jack.func.Functions;
import cn.thens.jack.ref.Ref;

public class StreamDemo {
    public void test(String text) {
        Ref.of(text).elvisRef("0")
                .safeApply(Integer::parseInt)
                .safeApply(it -> (long) it)
                .safeApply(it -> it + "safeCall")
                .get();
        int i = Functions.of(() -> 12).invoke();
        Functions.of(() -> System.out.println("hello, world")).run();
    }
}
