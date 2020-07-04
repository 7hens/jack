package cn.thens.jack.util;

import cn.thens.jack.func.Func0;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public final class JContract {
    public static void require(boolean value, Func0<Object> lazyMessage) {
        if (value) return;
        throw new IllegalArgumentException(lazyMessage.invoke().toString());
    }

    public static void require(boolean value, Object message) {
        require(value, () -> message);
    }

    public static void require(boolean value) {
        require(value, "Failed requirement");
    }

    public static <T> T requireNotNull(T value, Func0<Object> lazyMessage) {
        require(value != null, lazyMessage);
        return value;
    }

    public static <T> T requireNotNull(T value, Object message) {
        return requireNotNull(value, () -> message);
    }

    public static <T> T requireNotNull(T value) {
        return requireNotNull(value, "Required value was null");
    }

    public static void error(Object message) {
        throw new IllegalStateException(message.toString());
    }

    public static void error(Throwable error) {
        throw new RuntimeException(error);
    }
}
