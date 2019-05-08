package cn.thens.jack.util;

import cn.thens.jack.func.JFunc0;

@SuppressWarnings("WeakerAccess")
public final class JContract {
    public static void require(boolean value, JFunc0<Object> lazyMessage) {
        if (value) return;
        throw new IllegalArgumentException(lazyMessage.invoke().toString());
    }

    public static void require(boolean value, Object message) {
        require(value, () -> message);
    }

    public static void require(boolean value) {
        require(value, "Failed requirement");
    }

    public static <T> void requireNotNull(T value, JFunc0<Object> lazyMessage) {
        require(value != null, lazyMessage);
    }

    public static <T> void requireNotNull(T value, Object message) {
        requireNotNull(value, () -> message);
    }

    public static <T> void requireNotNull(T value) {
        requireNotNull(value, "Required value was null");
    }

    public static void error(Object message) {
        throw new IllegalStateException(message.toString());
    }
}
