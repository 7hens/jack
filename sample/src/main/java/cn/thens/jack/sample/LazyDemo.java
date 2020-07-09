package cn.thens.jack.sample;

import cn.thens.jack.ref.Ref;

public class LazyDemo {
    // 类似于 Kotlin 的 private val name by lazy { "Jack" }
    private Ref<String> name = Ref.lazy(() -> "Jack");

    public String getName() {
        return name.get();
    }
}
