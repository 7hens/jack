package cn.thens.jack.func;

public interface Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> {
    R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
            implements Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> {
        public abstract R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

        private X() {
        }

        public Func8.X<P2, P3, P4, P5, P6, P7, P8, P9, R> curry(P1 p1) {
            return Func8.X.of((p2, p3, p4, p5, p6, p7, p8, p9) ->
                    invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }

        public X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                    once.call(() -> invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9)));
        }

        public Action9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9> action() {
            return Action9.X.of(this::invoke);
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        of(Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
            return new X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>() {
                @Override
                public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
                    try {
                        return func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
