package cn.thens.jack.ref;

public abstract class MutRefKey<T, V> extends RefKey<T, V> {
    protected abstract void set(T target, V value);

    @Override
    public MutRefKey<T, V> defaultValue(V defaultValue) {
        MutRefKey<T, V> source = this;
        return new MutRefKey<T, V>() {
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

            @Override
            protected boolean exists(T target) {
                return source.exists(target);
            }
        };
    }
}
