package cn.thens.jack.ref;

import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Actions;

public abstract class RefKey<T, V> {
    protected abstract V get(T target, V defaultValue);

    protected abstract boolean exists(T target);

    protected V getDefaultValue() {
        return null;
    }

    public MutableRefKey<T, V> toMutableKey(Action2<? super T, ? super V> setter) {
        RefKey<T, V> self = this;
        return new MutableRefKey<T, V>() {
            @Override
            protected void set(T target, V value) {
                Actions.of(setter).run(target, value);
            }

            @Override
            protected V get(T target, V defaultValue) {
                return self.get(target, defaultValue);
            }

            @Override
            protected boolean exists(T target) {
                return self.exists(target);
            }

            @Override
            protected V getDefaultValue() {
                return self.getDefaultValue();
            }
        };
    }

    public RefKey<T, V> defaultValue(V defaultValue) {
        RefKey<T, V> source = this;
        return new RefKey<T, V>() {
            @Override
            protected V getDefaultValue() {
                return defaultValue;
            }

            @Override
            protected boolean exists(T target) {
                return source.exists(target);
            }

            @Override
            protected V get(T target, V defaultValue) {
                return source.get(target, defaultValue);
            }
        };
    }
}
