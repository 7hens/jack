package cn.thens.jack.ref;

import cn.thens.jack.func.Action1;
import cn.thens.jack.func.Actions;
import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.func.Once;
import cn.thens.jack.func.Predicate;
import cn.thens.jack.func.Values;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
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
        return Values.equals(valueOf(get()), valueOf(that));
    }

    private static Object valueOf(Object value) {
        if (value instanceof Ref) {
            return valueOf(((Ref<?>) value).get());
        }
        return value;
    }

    @Override
    public int hashCode() {
        return Values.hash(get());
    }

    @Override
    public String toString() {
        return Values.toString(get());
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
        return call(it -> it != null ? it : ref.asRef().get());
    }

    public Ref<T> elvisRef(T newValue) {
        return elvisRef(of(newValue));
    }

    public Ref<T> requireEquals(T value) {
        Values.require(equals(value));
        return this;
    }

    public Ref<T> requireNotNull() {
        Values.requireNotNull(get());
        return this;
    }

    public Ref<T> require(Predicate<? super Ref<? extends T>> predicate) {
        Values.require(Predicate.X.of(predicate).test(this));
        return this;
    }

    public boolean is(Class<?> clazz) {
        return Values.is(get(), clazz);
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
        return Funcs.of(func).call(this);
    }

    public <U> Ref<U> call(Func1<? super T, ? extends U> func) {
        return get(() -> Funcs.of(func).call(get()));
    }

    public <U> Ref<U> safeCall(Func1<? super T, ? extends U> func) {
        return isNotNull() ? call(func) : empty();
    }

    public Ref<T> run(Action1<? super T> action) {
        return get(() -> {
            T value = get();
            Actions.of(action).run(value);
            return value;
        });
    }

    public Ref<T> safeRun(Action1<? super T> action) {
        return isNotNull() ? run(action) : empty();
    }

    public MutRef<T> mutable() {
        return this instanceof MutRef ? (MutRef<T>) this : new MutRef<>(this);
    }

    public Ref<T> lazy() {
        Once<T> once = Once.create();
        return get(() -> once.call(this::get));
    }

    public MutRef<T> set(Action1<? super T> action) {
        Action1.X<? super T> actionX = Actions.of(action);
        return new MutRef<T>(this) {
            @Override
            public MutRef<T> set(Ref<T> ref) {
                actionX.run(ref.get());
                return super.set(ref);
            }
        };
    }

    public <V> boolean contains(RefKey<? super T, V> key) {
        try {
            return key.exists(get());
        } catch (Throwable e) {
            throw Values.wrap(e);
        }
    }

    public <V> Ref<T> put(MutRefKey<T, V> key, V value) {
        try {
            key.set(get(), value);
            return this;
        } catch (Throwable e) {
            throw Values.wrap(e);
        }
    }

    public <V> V get(RefKey<T, V> key, V defaultValue) {
        try {
            return key.get(get(), defaultValue);
        } catch (Throwable e) {
            throw Values.wrap(e);
        }
    }

    public <V> V get(RefKey<T, V> key) {
        try {
            return get(key, key.getDefaultValue());
        } catch (Throwable e) {
            throw Values.wrap(e);
        }
    }

    public <V> V getOrPut(MutRefKey<T, V> key, V defaultValue) {
        if (contains(key)) {
            return get(key);
        }
        put(key, defaultValue);
        return defaultValue;
    }

    public <V> Ref<T> putIfAbsent(MutRefKey<T, V> key, V value) {
        if (!contains(key)) put(key, value);
        return this;
    }

    @SuppressWarnings("rawtypes")
    private static final Ref EMPTY = new Ref<Object>() {
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
        final Func0.X<? extends T> funcX = Funcs.of(func);
        return new Ref<T>() {
            @Override
            public T get() {
                return funcX.call();
            }
        };
    }

    public static <T> Ref<T> obtain(IRef<? extends T> ref) {
        return get(() -> ref.asRef().get());
    }

    public static <T> Ref<T> lazy(Func0<? extends T> func) {
        return Ref.<T>get(func).lazy();
    }
}
