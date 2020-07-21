package cn.thens.jack.pack;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
public abstract class Codec<P, Q> {
    public abstract Q encode(P p);

    public abstract P decode(Q q);

    public AsyncCodec<P, Q> async() {
        Codec<P, Q> self = this;
        return new AsyncCodec<P, Q>() {
            @Override
            public Flow<Q> encode(P p) {
                return Flow.single(() -> self.encode(p));
            }

            @Override
            public Flow<P> decode(Q q) {
                return Flow.single(() -> self.decode(q));
            }
        };
    }
}
