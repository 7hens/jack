package cn.thens.jack.ref;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Functions;
import cn.thens.jack.func.Once;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked", "EqualsReplaceableByObjectsCall"})
public abstract class Ref<T> implements IRef<T> {
    abstract T get();

    @Override
    public Ref<T> asRef() {
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        Object thatValue = that;
        if (that instanceof Ref) {
            thatValue = ((Ref) that).get();
        }
        return equals(get(), thatValue);
    }

    @Override
    public int hashCode() {
        return isNotNull() ? get().hashCode() : 0;
    }

    @Override
    public String toString() {
        return isNull() ? "null" : get().toString();
    }

    public Class<?> type() {
        return get().getClass();
    }

    private static boolean equals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    public boolean isNotNull() {
        return get() != null;
    }

    public boolean isNull() {
        return get() == null;
    }

    public Ref<T> elvis(T newValue) {
        return elvisRef(of(newValue));
    }

    public Ref<T> elvisRef(IRef<T> ref) {
        return isNotNull() ? this : ref.asRef();
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

    public <U> Ref<U> apply(Func1<? super T, ? extends U> func) {
        return of(Functions.of(func).invoke(get()));
    }

    public <U> Ref<U> safeApply(Func1<? super T, ? extends U> func) {
        return isNotNull() ? apply(func) : empty();
    }

    public Ref<T> run(Action1<? super T> action) {
        Functions.of(action).run(get());
        return this;
    }

    public Ref<T> safeRun(Action1<? super T> action) {
        return isNotNull() ? run(action) : empty();
    }

    public MutableRef<T> mutable() {
        return this instanceof MutableRef ? (MutableRef<T>) this : new MutableRef<>(this);
    }

    public MutableRef<T> mutable(Action1<? super T> action) {
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

    public static <T> Ref<T> from(Func0<? extends T> func) {
        final Func0.X<? extends T> funcX = Functions.of(func);
        return new Ref<T>() {
            @Override
            public T get() {
                return funcX.invoke();
            }
        };
    }

    public static <T> Ref<T> lazy(Func0<? extends T> func) {
        Once<T> once = Once.create();
        return new Ref<T>() {
            @Override
            public T get() {
                return once.call(func);
            }
        };
    }
}
