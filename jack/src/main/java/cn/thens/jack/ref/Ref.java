package cn.thens.jack.ref;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Functions;
import cn.thens.jack.func.Once;
import cn.thens.jack.func.Predicate;
import cn.thens.jack.func.ThrowableWrapper;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked", "EqualsReplaceableByObjectsCall"})
public abstract class Ref<T> implements IRef<T> {
    public abstract T get();

    @Override
    public Ref<T> asRef() {
        return this;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        Object a = valueOf(get());
        Object b = valueOf(that);
        return a == null ? b == null : a.equals(b);
    }

    private static Object valueOf(Object value) {
        if (value instanceof Ref) {
            return valueOf(((Ref) value).get());
        }
        return value;
    }

    @Override
    public int hashCode() {
        return isNotNull() ? get().hashCode() : 0;
    }

    @Override
    public String toString() {
        return messageOf(get());
    }

    public Class<?> type() {
        return get().getClass();
    }

    public boolean isNotNull() {
        return get() != null;
    }

    public boolean isNull() {
        return get() == null;
    }

    public T elvis(T newValue) {
        return elvisRef(newValue).get();
    }

    public Ref<T> elvisRef(IRef<T> ref) {
        return isNotNull() ? this : ref.asRef();
    }

    public Ref<T> elvisRef(T newValue) {
        return elvisRef(of(newValue));
    }

    public Ref<T> requireEquals(T value) {
        require(equals(value));
        return this;
    }

    public Ref<T> requireNotNull() {
        require(isNotNull(), new NullPointerException());
        return this;
    }

    public Ref<T> require(Predicate<? super Ref<? extends T>> predicate) {
        require(Predicate.X.of(predicate).test(this));
        return this;
    }

    public boolean is(Class<?> clazz) {
        if (isNull()) return false;
        return clazz.isAssignableFrom(type());
    }

    public boolean isNot(Class<?> clazz) {
        return !is(clazz);
    }

    public <U> Ref<U> cast(Class<U> clazz) {
        if (is(clazz)) return (Ref<U>) this;
        throw new ClassCastException("expected " + clazz + ", but found " + type());
    }

    public <U> Ref<U> safeCast(Class<U> clazz) {
        if (is(clazz)) return (Ref<U>) this;
        return empty();
    }

    public boolean in(Iterable<T> iterable) {
        for (T item : iterable) {
            if (equals(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean in(T... items) {
        for (T item : items) {
            if (equals(item)) {
                return true;
            }
        }
        return false;
    }

    public <U> U to(Func1<? super Ref<T>, ? extends U> func) {
        return Functions.of(func).invoke(this);
    }

    public <U> Ref<U> apply(Func1<? super T, ? extends U> func) {
        return get(() -> Functions.of(func).invoke(get()));
    }

    public <U> Ref<U> safeApply(Func1<? super T, ? extends U> func) {
        return isNotNull() ? apply(func) : empty();
    }

    public Ref<T> run(Action1<? super T> action) {
        return get(() -> {
            T value = get();
            Functions.of(action).run(value);
            return value;
        });
    }

    public Ref<T> safeRun(Action1<? super T> action) {
        return isNotNull() ? run(action) : empty();
    }

    public MutableRef<T> mutable() {
        return this instanceof MutableRef ? (MutableRef<T>) this : new MutableRef<>(this);
    }

    public Ref<T> lazy() {
        Once<T> once = Once.create();
        return get(() -> once.call(this::get));
    }

    public MutableRef<T> set(Action1<? super T> action) {
        Action1.X<? super T> actionX = Functions.of(action);
        return new MutableRef<T>(this) {
            @Override
            public MutableRef<T> set(Ref<T> ref) {
                actionX.run(ref.get());
                return super.set(ref);
            }
        };
    }

    public <V> boolean contains(RefKey<T, V> key) {
        return key.exists(get());
    }

    public <V> Ref<T> put(MutableRefKey<T, V> key, V value) {
        key.set(get(), value);
        return this;
    }

    public <V> V get(RefKey<T, V> key, V defaultValue) {
        return key.get(get(), defaultValue);
    }

    public <V> V get(RefKey<T, V> key) {
        return get(key, key.getDefaultValue());
    }

    public <V> V getOrPut(MutableRefKey<T, V> key, V defaultValue) {
        if (contains(key)) {
            return get(key);
        }
        put(key, defaultValue);
        return defaultValue;
    }

    public <V> Ref<T> putIfAbsent(MutableRefKey<T, V> key, V value) {
        if (!contains(key)) put(key, value);
        return this;
    }

    private static final Ref EMPTY = new Ref() {
        @Override
        public Object get() {
            return null;
        }
    };

    public static <T> Ref<T> empty() {
        return EMPTY;
    }

    public static <T> Ref<T> of(final T value) {
        if (value == null) return empty();
        return new Ref<T>() {
            @Override
            public T get() {
                return value;
            }
        };
    }

    public static <T> Ref<T> get(Func0<? extends T> func) {
        final Func0.X<? extends T> funcX = Functions.of(func);
        return new Ref<T>() {
            @Override
            public T get() {
                return funcX.invoke();
            }
        };
    }

    public static <T> Ref<T> lazy(Func0<? extends T> func) {
        return Ref.<T>get(func).lazy();
    }

    public static void require(boolean value, Object message) {
        if (value) return;
        if (message instanceof RuntimeException) {
            throw (RuntimeException) message;
        }
        if (message instanceof Throwable) {
            throw ThrowableWrapper.of((Throwable) message);
        }
        throw new IllegalArgumentException(messageOf(message));
    }

    public static void require(boolean value) {
        require(value, "Failed requirement");
    }

    private static String messageOf(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return (String) obj;
        if (obj instanceof Func0) return messageOf(Functions.of((Func0) obj).invoke());
        if (obj instanceof Throwable) return messageOf((Throwable) obj);
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

    private static String messageOf(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, false);
        throwable.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
