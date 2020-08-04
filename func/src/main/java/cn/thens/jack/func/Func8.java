package cn.thens.jack.func;

public interface Func8<P1, P2, P3, P4, P5, P6, P7, P8, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, P7, P8, R>
            implements Func8<P1, P2, P3, P4, P5, P6, P7, P8, R>, Action8<P1, P2, P3, P4, P5, P6, P7, P8> {
        @Override
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
            call(p1, p2, p3, p4, p5, p6, p7, p8);
        }

        private X() {
        }

        public Func7.X<P2, P3, P4, P5, P6, P7, P8, R> curry(P1 p1) {
            return Func7.X.of((p2, p3, p4, p5, p6, p7, p8) ->
                    call(p1, p2, p3, p4, p5, p6, p7, p8));
        }

        public Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6, p7, p8) ->
                    once.call(() -> call(p1, p2, p3, p4, p5, p6, p7, p8)));
        }

        public Action8.X<P1, P2, P3, P4, P5, P6, P7, P8> action() {
            return Action8.X.of(this);
        }

        public Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R>
        doFirst(Action8<P1, P2, P3, P4, P5, P6, P7, P8> action) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8) -> {
                action.run(p1, p2, p3, p4, p5, p6, p7, p8);
                return call(p1, p2, p3, p4, p5, p6, p7, p8);
            });
        }

        public Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R>
        doLast(Action8<P1, P2, P3, P4, P5, P6, P7, P8> action) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8) -> {
                R result = call(p1, p2, p3, p4, p5, p6, p7, p8);
                action.run(p1, p2, p3, p4, p5, p6, p7, p8);
                return result;
            });
        }

        public <R2> Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R2> to(Func1<? super R, ? extends R2> func) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8) ->
                    func.call(call(p1, p2, p3, p4, p5, p6, p7, p8)));
        }

        public Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R> run(Action1<? super R> action) {
            return of((p1, p2, p3, p4, p5, p6, p7, p8) -> {
                R result = call(p1, p2, p3, p4, p5, p6, p7, p8);
                action.run(result);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
        Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R>
        of(Func8<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8, ? extends R> func) {
            return new Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
                    try {
                        return func.call(p1, p2, p3, p4, p5, p6, p7, p8);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
