package cn.thens.jack.pack;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
class PackCache<V> extends Pack<V> {
    private final Pack<V> pack;
    private final Pack<V> cache;

    PackCache(Pack<V> pack, Pack<V> cache) {
        this.pack = pack;
        this.cache = cache;
    }

    @Override
    public Flow<V> get() {
        return cache.get().catchError(e -> {
            return pack.get().onEach(value -> {
                cache.put(value).collect();
            });
        });
    }

    @Override
    public Flow<Void> put(V value) {
        return pack.put(value).onComplete(() -> {
            cache.put(value).collect();
        });
    }
}
