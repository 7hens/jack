package cn.thens.jack.ref;

public abstract class MutableRefKey<T, V> extends RefKey<T, V> {
    protected abstract void set(T target, V value);

    @Override
    public MutableRefKey<T, V> defaultValue(V defaultValue) {
        MutableRefKey<T, V> source = this;
        return new MutableRefKey<T, V>() {
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
