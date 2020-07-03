package cn.thens.jack;

/**
 * @author 7hens
 */
public final class Wrapper<T> {
    private final T base;

    private Wrapper(T base) {
        this.base = base;
    }

    public static <T> Wrapper<T> of(T base) {
        return new Wrapper<>(base);
    }

    public T build() {
        return base;
    }

    public <V> Wrapper<T> put(Key<T, V> key, V value) {
        key.set(base, value);
        return this;
    }

    public <V> V get(Key<T, V> key, V defaultValue) {
        return key.get(base, defaultValue);
    }

    public <V> V get(Key<T, V> key) {
        return get(key, key.getDefaultValue());
    }

    public static abstract class Key<T, V> {
        protected abstract void set(T target, V value);

        protected abstract V get(T target, V defaultValue);

        protected V getDefaultValue() {
            return null;
        }

        public final Key<T, V> defaultValue(V defaultValue) {
            Key<T, V> source = this;
            return new Key<T, V>() {
                @Override
                protected void set(T target, V value) {
                    source.set(target, value);
                }

                @Override
                protected V getDefaultValue() {
                    return defaultValue;
                }

                @Override
                protected V get(T target, V defaultValue) {
                    return source.get(target, defaultValue);
                }
            };
        }
    }
}
