package cn.thens.jack.flow;

import org.jetbrains.annotations.ApiStatus;

/**
 * @author 7hens
 */
@ApiStatus.Experimental
public abstract class MapFlow<K, V> extends Flow<MapFlow.Entry<K, V>> {

    public PolyFlow<V> toPoly() {
        return to(FlowX.poly());
    }

    @ApiStatus.Experimental
    public static abstract class Entry<K, V> extends Flow<V> {
        private final K key;

        public Entry(K key) {
            this.key = key;
        }

        public K getKey() {
            return key;
        }
    }
}
