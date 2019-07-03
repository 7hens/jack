package cn.thens.jack.init;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.property.JGetter;
import cn.thens.jack.util.JContract;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class JInit implements JGetter<Boolean> {
    private AtomicBoolean isInitialized = new AtomicBoolean(false);

    private JInit() {
    }

    public static JInit create() {
        return new JInit();
    }

    @Override
    public Boolean get() {
        return !isInitialized.compareAndSet(false, true);
    }

    public void check() {
        JContract.require(peek(), "JInit is uninitialized");
    }

    public boolean peek() {
        return isInitialized.get();
    }
}
