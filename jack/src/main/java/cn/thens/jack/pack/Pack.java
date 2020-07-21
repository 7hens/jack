package cn.thens.jack.pack;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
public abstract class Pack<V> {
    public abstract Flow<V> get();

    public abstract Flow<Void> put(V value);

    public Pack<V> cache(Pack<V> cache) {
        return new PackCache<>(this, cache);
    }

    public static <V> Pack<V> weakRef() {
        return new PackWeakRef<>();
    }

    public static <V> Pack<V> strongRef() {
        return new PackStrongRef<>();
    }
}
