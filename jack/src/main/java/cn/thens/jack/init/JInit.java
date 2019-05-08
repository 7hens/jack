package cn.thens.jack.init;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.property.JGetter;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class JInit implements JGetter<Boolean> {
    private AtomicBoolean isInitialized = new AtomicBoolean(false);

    @Override
    public Boolean get() {
        return !isInitialized.compareAndSet(false, true);
    }

    public void check() {
        if (peek()) return;
        throw new RuntimeException("JInit is uninitialized");
    }

    public boolean peek() {
        return isInitialized.get();
    }
}