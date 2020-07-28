package cn.thens.jack.pack;

import org.jetbrains.annotations.ApiStatus;

import cn.thens.jack.flow.Flow;
import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;

/**
 * @author 7hens
 */
@ApiStatus.Experimental
public interface Codec<P, Q> {
    Q encode(P p) throws Throwable;

    P decode(Q q) throws Throwable;

    abstract class X<P, Q> implements Codec<P, Q> {
        @Override
        public abstract Q encode(P p);

        @Override
        public abstract P decode(Q q);

        public PackConverter<P, Q> transformer() {
            Codec<P, Q> self = this;
            return new PackConverter<P, Q>() {
                @Override
                public Flow<Q> convert(P p) {
                    return Flow.get(() -> self.encode(p));
                }

                @Override
                public Flow<P> invert(Q q) {
                    return Flow.get(() -> self.decode(q));
                }
            };
        }

        public static <P, Q> X<P, Q> create(Func1<? super P, ? extends Q> encodeFunc,
                                            Func1<? super Q, ? extends P> decodeFunc) {
            final Func1.X<P, Q> encodeFuncX = Funcs.of(encodeFunc);
            final Func1.X<Q, P> decodeFuncX = Funcs.of(decodeFunc);
            return new X<P, Q>() {
                @Override
                public Q encode(P p) {
                    return encodeFuncX.call(p);
                }

                @Override
                public P decode(Q q) {
                    return decodeFuncX.call(q);
                }
            };
        }
    }
}
