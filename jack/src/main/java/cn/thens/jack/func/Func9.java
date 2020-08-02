package cn.thens.jack.func;

public interface Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
            implements Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>, Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
        @Override
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
            call(p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        private X() {
        }

        public Func8.X<P2, P3, P4, P5, P6, P7, P8, P9, R> curry(P1 p1) {
            return Func8.X.of((p2, p3, p4, p5, p6, p7, p8, p9) ->
                    call(p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }

        public Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                    once.call(() -> call(p1, p2, p3, p4, p5, p6, p7, p8, p9)));
        }

        public Action9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9> action() {
            return Action9.X.of(this);
        }

        public Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        doFirst(Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> action) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> {
                action.run(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                return call(p1, p2, p3, p4, p5, p6, p7, p8, p9);
            });
        }

        public Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        doLast(Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> action) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> {
                R result = call(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                action.run(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                return result;
            });
        }

        public <R2> Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R2>
        to(Func1<? super R, ? extends R2> func) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                    func.call(call(p1, p2, p3, p4, p5, p6, p7, p8, p9)));
        }

        public Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        run(Action1<? super R> action) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> {
                R result = call(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                action.run(result);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
        of(Func9<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8, ? super P9, ? extends R> func) {
            return new Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
                    try {
                        return func.call(p1, p2, p3, p4, p5, p6, p7, p8, p9);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
