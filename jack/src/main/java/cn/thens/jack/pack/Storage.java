package cn.thens.jack.pack;

import org.jetbrains.annotations.ApiStatus;

import java.io.File;

import cn.thens.jack.flow.Flow;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;

/**
 * @author 7hens
 */
@ApiStatus.Experimental
public abstract class Storage<K, V> {
    public abstract Pack<V> pick(K key);

    public Storage<K, V> cache(Storage<K, V> cache) {
        Storage<K, V> upStorage = this;
        return new Storage<K, V>() {
            @Override
            public Pack<V> pick(K key) {
                return upStorage.pick(key).cache(cache.pick(key));
            }
        };
    }

    public <V2> Storage<K, V2> transform(PackConverter<V, V2> converter) {
        Storage<K, V> upStorage = this;
        return new Storage<K, V2>() {
            @Override
            public Pack<V2> pick(K key) {
                return upStorage.pick(key).transform(converter);
            }
        };
    }

    public <K2> Storage<K2, V> liftKey(Func1<? super K2, ? extends K> func) {
        Storage<K, V> upStorage = this;
        Func1.X<? super K2, ? extends K> funcX = Funcs.of(func);
        return new Storage<K2, V>() {
            @Override
            public Pack<V> pick(K2 key) {
                return upStorage.pick(funcX.call(key));
            }
        };
    }

    // TODO Storage.file
    private static Storage<File, byte[]> file() {
        return new Storage<File, byte[]>() {
            @Override
            public Pack<byte[]> pick(File key) {
                return new Pack<byte[]>() {
                    @Override
                    public Flow<byte[]> get() {
                        return null;
                    }

                    @Override
                    public Flow<Void> put(byte[] value) {
                        return null;
                    }
                };
            }
        };
    }
}
