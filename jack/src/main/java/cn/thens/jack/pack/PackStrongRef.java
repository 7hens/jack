package cn.thens.jack.pack;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
class PackStrongRef<V> extends Pack<V> {
    private V ref = null;

    @Override
    public Flow<V> get() {
        return Flow.create(emitter -> {
            if (ref == null) {
                throw new NullPointerException();
            }
            emitter.data(ref);
            emitter.complete();
        });
    }

    @Override
    public Flow<Void> put(V value) {
        return Flow.create(emitter -> {
            this.ref = value;
            emitter.complete();
        });
    }
}
