package cn.thens.jack.flow;

/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public class BackPressureException extends RuntimeException {
    public BackPressureException(Throwable cause) {
        super(cause);
    }
}
