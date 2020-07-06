package cn.thens.jack.ref;

import java.util.Map;

/**
 * @author 7hens
 */
public abstract class MapKey<K, V> extends MutableRefKey<Map<K, V>, V> {
    public static <K, V> MapKey<K, V> create(K key) {
        return new MapKey<K, V>() {
            @Override
            protected void set(Map<K, V> target, V value) {
                target.put(key, value);
            }

            @Override
            protected V get(Map<K, V> target, V defaultValue) {
                return exists(target) ? target.get(key) : defaultValue;
            }

            @Override
            protected boolean exists(Map<K, V> target) {
                return target.containsKey(key);
            }
        };
    }
}
