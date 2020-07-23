package cn.thens.jack.func;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author 7hens
 */
public final class Things {
    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... objects) {
        return Arrays.hashCode(objects);
    }

    public static <T> T elvis(T value, T fallback) {
        return value != null ? value : fallback;
    }

    public static void require(boolean value, Object message) {
        if (value) return;
        if (message instanceof RuntimeException) {
            throw (RuntimeException) message;
        }
        if (message instanceof Throwable) {
            throw wrap((Throwable) message);
        }
        throw new IllegalArgumentException(toString(message));
    }

    public static void require(boolean value) {
        require(value, "Failed requirement");
    }

    public static <T> T requireNotNull(T value) {
        require(value != null, new NullPointerException());
        return value;
    }

    public static <T> int compare(T a, T b, Comparator<? super T> comparator) {
        return a == b ? 0 : Comparator.X.of(comparator).compare(a, b);
    }

    public static boolean is(Object obj, Class<?> cls) {
        return obj != null && cls.isAssignableFrom(obj.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> T safeCast(Object obj, Class<T> cls) {
        return is(obj, cls) ? (T) obj : null;
    }

    public static Type type(final Class<?> rawType, final Type... typeArgs) {
        return new ParameterizedType() {
            @Override
            public Type getRawType() {
                return rawType;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return typeArgs;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    public static String toString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return (String) obj;
        if (obj instanceof Func0) return toString(Funcs.of((Func0<?>) obj).call());
        if (obj instanceof Throwable) return getStackTraceString((Throwable) obj);
        if (!obj.getClass().isArray()) return obj.toString();
        if (obj instanceof boolean[]) return Arrays.toString((boolean[]) obj);
        if (obj instanceof byte[]) return Arrays.toString((byte[]) obj);
        if (obj instanceof char[]) return Arrays.toString((char[]) obj);
        if (obj instanceof short[]) return Arrays.toString((short[]) obj);
        if (obj instanceof int[]) return Arrays.toString((int[]) obj);
        if (obj instanceof long[]) return Arrays.toString((long[]) obj);
        if (obj instanceof float[]) return Arrays.toString((float[]) obj);
        if (obj instanceof double[]) return Arrays.toString((double[]) obj);
        if (obj instanceof Object[]) return Arrays.deepToString((Object[]) obj);
        return obj.toString();
    }

    private static String getStackTraceString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, false);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static RuntimeException wrap(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new ThrowableWrapper(e);
    }

    public static Throwable unwrap(Throwable e) {
        if (e instanceof ThrowableWrapper) {
            return unwrap(e.getCause());
        }
        return e;
    }

    private static final class ThrowableWrapper extends RuntimeException {
        private ThrowableWrapper(Throwable cause) {
            super(cause);
        }
    }
}
