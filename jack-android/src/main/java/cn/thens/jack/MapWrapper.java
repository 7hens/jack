package cn.thens.jack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 7hens
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class MapWrapper<K, V> {
    private final Map<K, V> map;

    private MapWrapper(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapWrapper<K, V> wrap(Map<K, V> map) {
        return new MapWrapper<>(map);
    }

    public static <K, V> MapWrapper<K, V> create() {
        return wrap(new HashMap<>());
    }

    public Map<K, V> build() {
        return map;
    }

    public MapWrapper<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapWrapper<K, V> putIfAbsent(K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
        return this;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MapWrapper) {
            return ((MapWrapper) obj).map.equals(map);
        }
        return this == obj;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
