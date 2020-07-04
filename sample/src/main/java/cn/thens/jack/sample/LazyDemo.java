package cn.thens.jack.sample;

import cn.thens.jack.property.Getter;
import cn.thens.jack.property.Lazy;

public class LazyDemo {
    // 类似于 Kotlin 的 private val name by lazy { "Jack" }
    private Getter<String> name = Lazy.of(() -> "Jack");

    public String getName() {
        return name.get();
    }
}
