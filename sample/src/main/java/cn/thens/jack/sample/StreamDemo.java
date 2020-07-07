package cn.thens.jack.sample;

import java.util.List;

import cn.thens.jack.func.Functions;
import cn.thens.jack.ref.Ref;
import cn.thens.jack.util.JSequence;

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

    public List<Integer> stream() {
        return JSequence.of(1, 2, 3)
                .map(it -> it + 2)
                .toList();
    }
}
