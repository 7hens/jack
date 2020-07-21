package cn.thens.jack.pack;

import cn.thens.jack.flow.Flow;
import cn.thens.jack.scheduler.Scheduler;

/**
 * @author 7hens
 */
public abstract class AsyncCodec<P, Q> {
    public abstract Flow<Q> encode(P p);

    public abstract Flow<P> decode(Q q);

    public AsyncCodec<Q, P> inverted() {
        AsyncCodec<P, Q> self = this;
        return new AsyncCodec<Q, P>() {
            @Override
            public Flow<P> encode(Q q) {
                return self.decode(q);
            }

            @Override
            public Flow<Q> decode(P q) {
                return self.encode(q);
            }
        };
    }

    public <R> AsyncCodec<P, R> map(AsyncCodec<Q, R> codec) {
        AsyncCodec<P, Q> self = this;
        return new AsyncCodec<P, R>() {
            @Override
            public Flow<R> encode(P p) {
                return self.encode(p).flatMap(codec::encode);
            }

            @Override
            public Flow<P> decode(R q) {
                return codec.decode(q).flatMap(self::decode);
            }
        };
    }

    public AsyncCodec<P, Q> catchError(AsyncCodec<P, Q> fallback) {
        AsyncCodec<P, Q> self = this;
        return new AsyncCodec<P, Q>() {
            @Override
            public Flow<Q> encode(P p) {
                return self.encode(p).catchError(it -> fallback.encode(p));
            }

            @Override
            public Flow<P> decode(Q q) {
                return self.decode(q).catchError(it -> fallback.decode(q));
            }
        };
    }

    public AsyncCodec<P, Q> flowOn(Scheduler scheduler) {
        AsyncCodec<P, Q> self = this;
        return new AsyncCodec<P, Q>() {
            @Override
            public Flow<Q> encode(P p) {
                return self.encode(p).flowOn(scheduler);
            }

            @Override
            public Flow<P> decode(Q q) {
                return self.decode(q).flowOn(scheduler);
            }
        };
    }
}
