package cn.thens.jack.pack;

import org.jetbrains.annotations.ApiStatus;

import cn.thens.jack.flow.Flow;
import cn.thens.jack.scheduler.Scheduler;

/**
 * @author 7hens
 */
@ApiStatus.Experimental
public abstract class PackConverter<P, Q> {
    public abstract Flow<Q> convert(P p);

    public abstract Flow<P> invert(Q q);

    public PackConverter<Q, P> inverted() {
        PackConverter<P, Q> self = this;
        return new PackConverter<Q, P>() {
            @Override
            public Flow<P> convert(Q q) {
                return self.invert(q);
            }

            @Override
            public Flow<Q> invert(P q) {
                return self.convert(q);
            }
        };
    }

    public <R> PackConverter<P, R> map(PackConverter<Q, R> codec) {
        PackConverter<P, Q> self = this;
        return new PackConverter<P, R>() {
            @Override
            public Flow<R> convert(P p) {
                return self.convert(p).flatMap(codec::convert);
            }

            @Override
            public Flow<P> invert(R q) {
                return codec.invert(q).flatMap(self::invert);
            }
        };
    }

    public PackConverter<P, Q> catchError(PackConverter<P, Q> fallback) {
        PackConverter<P, Q> self = this;
        return new PackConverter<P, Q>() {
            @Override
            public Flow<Q> convert(P p) {
                return self.convert(p).catchError(it -> fallback.convert(p));
            }

            @Override
            public Flow<P> invert(Q q) {
                return self.invert(q).catchError(it -> fallback.invert(q));
            }
        };
    }

    public PackConverter<P, Q> flowOn(Scheduler scheduler) {
        PackConverter<P, Q> self = this;
        return new PackConverter<P, Q>() {
            @Override
            public Flow<Q> convert(P p) {
                return self.convert(p).flowOn(scheduler);
            }

            @Override
            public Flow<P> invert(Q q) {
                return self.invert(q).flowOn(scheduler);
            }
        };
    }
}
