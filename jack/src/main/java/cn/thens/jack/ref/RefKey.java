package cn.thens.jack.ref;

import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Actions;

public abstract class RefKey<T, V> {
    protected abstract V get(T target, V defaultValue) throws Throwable;

    protected abstract boolean exists(T target) throws Throwable;

    protected V getDefaultValue() throws Throwable {
        return null;
    }

    public MutRefKey<T, V> toMutableKey(Action2<? super T, ? super V> setter) {
        RefKey<T, V> self = this;
        return new MutRefKey<T, V>() {
            @Override
            protected void set(T target, V value) {
                Actions.of(setter).run(target, value);
            }

            @Override
            protected V get(T target, V defaultValue) throws Throwable {
                return self.get(target, defaultValue);
            }

            @Override
            protected boolean exists(T target) throws Throwable {
                return self.exists(target);
            }

            @Override
            protected V getDefaultValue() throws Throwable {
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
            protected boolean exists(T target) throws Throwable {
                return source.exists(target);
            }

            @Override
            protected V get(T target, V defaultValue) throws Throwable {
                return source.get(target, defaultValue);
            }
        };
    }
}
