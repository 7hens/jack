package cn.thens.jack.pack;

import java.lang.ref.WeakReference;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
class PackWeakRef<V> extends Pack<V> {
    private WeakReference<V> ref = new WeakReference<>(null);

    @Override
    public Flow<V> get() {
        return Flow.create(emitter -> {
            V value = ref.get();
            if (value == null) {
                throw new NullPointerException();
            }
            emitter.data(value);
            emitter.complete();
        });
    }

    @Override
    public Flow<Void> put(V value) {
        return Flow.create(emitter -> {
            ref = new WeakReference<>(value);
            emitter.complete();
        });
    }
}
