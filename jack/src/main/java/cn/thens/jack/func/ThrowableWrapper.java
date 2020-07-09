package cn.thens.jack.func;

public class ThrowableWrapper extends RuntimeException {
    private ThrowableWrapper(Throwable cause) {
        super(cause);
    }

    public static ThrowableWrapper of(Throwable cause) {
        if (cause instanceof ThrowableWrapper) {
            return (ThrowableWrapper) cause;
        }
        return new ThrowableWrapper(cause);
    }
}
