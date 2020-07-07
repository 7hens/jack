package cn.thens.jack.func;

public interface Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
    void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, P7, P8, P9>
            implements Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
        public abstract void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

        private X() {
        }

        public Action8.X<P2, P3, P4, P5, P6, P7, P8, P9> curry(P1 p1) {
            return Action8.X.of((p2, p3, p4, p5, p6, p7, p8, p9) ->
                    run(p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }

        public X<P1, P2, P3, P4, P5, P6, P7, P8, P9> once() {
            final Once<Void> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                    once.run(() -> run(p1, p2, p3, p4, p5, p6, p7, p8, p9)));
        }

        public <R> Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func(R result) {
            return Func9.X.of((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> {
                run(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
        X<P1, P2, P3, P4, P5, P6, P7, P8, P9>
        of(Action9<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8, ? super P9> action) {
            return new X<P1, P2, P3, P4, P5, P6, P7, P8, P9>() {
                @Override
                public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
                    try {
                        action.run(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
