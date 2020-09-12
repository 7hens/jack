package cn.thens.jack.flow;

import java.nio.BufferOverflowException;

/**
 * @author 7hens
 */
@SuppressWarnings("unchecked")
public final class BackPressures {

    public static <T> BackPressure.X<T> buffer(int capacity) {
        return of(buffer -> {
            if (buffer.size() > capacity) {
                throw new BufferOverflowException();
            }
        });
    }

    public static <T> BackPressure.X<T> error() {
        return ERROR;
    }

    private static BackPressure.X ERROR = of(buffer -> {
        throw new BufferOverflowException();
    });

    public static <T> BackPressure.X<T> success() {
        return SUCCESS;
    }

    private static BackPressure.X SUCCESS = of(buffer -> {
    });

    public static <T> BackPressure.X<T> of(BackPressure<T> backpressure) {
        return BackPressure.X.of(backpressure);
    }
}
