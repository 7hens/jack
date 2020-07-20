package cn.thens.jack.pack;

import cn.thens.jack.func.Exceptions;

/**
 * @author 7hens
 */
public interface Codec<P, Q> {
    Q encode(P p) throws Throwable;

    P decode(Q q) throws Throwable;

    abstract class X<P, Q> implements Codec<P, Q> {
        @Override
        public abstract Q encode(P p);

        @Override
        public abstract P decode(Q q);

        public X<Q, P> inverted() {
            X<P, Q> self = this;
            return new X<Q, P>() {
                @Override
                public P encode(Q q) {
                    return self.decode(q);
                }

                @Override
                public Q decode(P q) {
                    return self.encode(q);
                }
            };
        }

        public <R> X<P, R> then(Codec<Q, R> codec) {
            X<P, Q> self = this;
            X<Q, R> codecX = of(codec);
            return new X<P, R>() {
                @Override
                public R encode(P p) {
                    return codecX.encode(self.encode(p));
                }

                @Override
                public P decode(R q) {
                    return self.decode(codecX.decode(q));
                }
            };
        }

        private static <P, Q> X<P, Q> of(Codec<P, Q> codec) {
            return new X<P, Q>() {
                @Override
                public Q encode(P p) {
                    try {
                        return codec.encode(p);
                    } catch (Throwable e) {
                        throw Exceptions.wrap(e);
                    }
                }

                @Override
                public P decode(Q q) {
                    try {
                        return codec.decode(q);
                    } catch (Throwable e) {
                        throw Exceptions.wrap(e);
                    }
                }
            };
        }

        public static <P, Q> Codec<P, Q> create() {
            return new Codec<P, Q>() {
                @Override
                public Q encode(P p) {
                    return null;
                }

                @Override
                public P decode(Q q) {
                    return null;
                }
            };
        }
    }
}
