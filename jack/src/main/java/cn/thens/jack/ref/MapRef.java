package cn.thens.jack.ref;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MapRef<K, V> extends Ref<Map<K, V>> {
    public static <K, V> MapRef<K, V> of(Map<K, V> map) {
        return new MapRef<K, V>() {
            @Override
            public Map<K, V> get() {
                return map;
            }
        };
    }

    public static <K, V> MapRef<K, V> hash() {
        return of(new HashMap<>());
    }

    public static <K, V> MapRef<K, V> concurrent() {
        return of(new ConcurrentHashMap<>());
    }

    public static <K, V> MapRef<K, V> self(Ref<? extends Map<K, V>> ref) {
        return of(ref.get());
    }

    public boolean contains(K key) {
        return get().containsKey(key);
    }

    public MapRef<K, V> put(K key, V value) {
        get().put(key, value);
        return this;
    }

    public MapRef<K, V> putIfAbsent(K key, V value) {
        Map<K, V> map = get();
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
        return this;
    }

    public V get(K key) {
        return get().get(key);
    }

    public V get(K key, V defaultValue) {
        Map<K, V> map = get();
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    public V getOrPut(K key, V defaultValue) {
        Map<K, V> map = get();
        if (map.containsKey(key)) {
            return map.get(key);
        }
        map.put(key, defaultValue);
        return defaultValue;
    }
}
