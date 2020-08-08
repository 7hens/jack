package cn.thens.jack.func;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public final class Values {
    public static final boolean TRUE = Boolean.parseBoolean("true");
    public static final boolean FALSE = Boolean.parseBoolean("false");

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... objects) {
        return Arrays.hashCode(objects);
    }

    public static <T> T elvis(@Nullable T value, @Nullable T fallback) {
        return value != null ? value : fallback;
    }

    public static <T> int compare(@Nullable T a, @Nullable T b, @NotNull Comparator<? super T> comparator) {
        return a == b ? 0 : Comparator.X.of(comparator).compare(a, b);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<?>> int compare(@Nullable T a, @Nullable T b) {
        if (a == b) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return ((Comparable<Object>) a).compareTo(b);
    }

    public static boolean is(@Nullable Object obj, @NotNull Class<?> cls) {
        return cls.isInstance(obj);
    }

    public static boolean isNull(@Nullable Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(@Nullable Object obj) {
        return obj != null;
    }

    public static boolean isEmpty(@Nullable Iterable<?> iterable) {
        return iterable == null || iterable.iterator().hasNext();
    }

    public static boolean isNotEmpty(@Nullable Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    public static <T> boolean isEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(@Nullable T[] array) {
        return !isEmpty(array);
    }

    public static <T> boolean isEmpty(@Nullable CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static <T> boolean isNotEmpty(@Nullable CharSequence text) {
        return !isEmpty(text);
    }

    @Nullable
    public static <T> T safeCast(@Nullable Object obj, @NotNull Class<T> cls) {
        return is(obj, cls) ? cls.cast(obj) : null;
    }

    @NotNull
    public static Type type(@NotNull final Class<?> rawType, @NotNull final Type... typeArgs) {
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

    public static boolean require(boolean value, @Nullable Object message) {
        if (value) return true;
        if (message instanceof RuntimeException) {
            throw (RuntimeException) message;
        }
        if (message instanceof Throwable) {
            throw wrap((Throwable) message);
        }
        throw new IllegalArgumentException(toString(message));
    }

    public static boolean require(boolean value, @NotNull Func0<?> message) {
        if (value) return true;
        return require(false, Funcs.of(message).call());
    }

    public static boolean require(boolean value) {
        return require(value, "Failed requirement");
    }

    @NotNull
    public static <T> T requireNotNull(@Nullable T value) {
        require(value != null, new NullPointerException());
        //noinspection ConstantConditions
        return value;
    }

    @NotNull
    public static String toString(@Nullable Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return (String) obj;
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

    @NotNull
    public static RuntimeException wrap(@NotNull Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new ThrowableWrapper(e);
    }

    @NotNull
    public static Throwable unwrap(@NotNull Throwable e) {
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
