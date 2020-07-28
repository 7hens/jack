package cn.thens.jack.pack;

import org.jetbrains.annotations.ApiStatus;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
@ApiStatus.Experimental
public abstract class Pack<V> {
    public abstract Flow<V> get();

    public abstract Flow<Void> put(V value);

    public Pack<V> cache(Pack<V> cache) {
        return new PackCache<>(this, cache);
    }

    public <V2> Pack<V2> transform(PackConverter<V, V2> converter) {
        Pack<V> upPack = this;
        return new Pack<V2>() {
            @Override
            public Flow<V2> get() {
                return upPack.get().flatMap(converter::convert);
            }

            @Override
            public Flow<Void> put(V2 value) {
                return converter.invert(value).flatMap(upPack::put);
            }
        };
    }

    public static <V> Pack<V> weakRef() {
        return new PackWeakRef<>();
    }

    public static <V> Pack<V> strongRef() {
        return new PackStrongRef<>();
    }
}
