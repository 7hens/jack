package cn.thens.jack.sample;

import cn.thens.jack.JGetter;
import cn.thens.jack.JLazy;

public class LazyDemo {
    // 类似于 Kotlin 的 private val name by lazy { "Jack" }
    private JGetter<String> name = JLazy.create(() -> "Jack");

    public String getName() {
        return name.get();
    }
}
