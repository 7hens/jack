package cn.thens.jack.util;

public class ThrowableWrapper extends RuntimeException {
    public ThrowableWrapper(Throwable cause) {
        super(cause);
    }
}
