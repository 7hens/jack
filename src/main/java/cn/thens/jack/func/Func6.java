package cn.thens.jack.func;

public interface Func6<P1, P2, P3, P4, P5, P6, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, R>
            implements Func6<P1, P2, P3, P4, P5, P6, R>, Action6<P1, P2, P3, P4, P5, P6> {
        @Override
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
            call(p1, p2, p3, p4, p5, p6);
        }

        private X() {
        }

        public Func5.X<P2, P3, P4, P5, P6, R> curry(P1 p1) {
            return Func5.X.of((p2, p3, p4, p5, p6) ->
                    call(p1, p2, p3, p4, p5, p6));
        }

        public Func6.X<P1, P2, P3, P4, P5, P6, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6) -> once.call(() -> call(p1, p2, p3, p4, p5, p6)));
        }

        public Action6.X<P1, P2, P3, P4, P5, P6> action() {
            return Action6.X.of(this);
        }

        public Func6.X<P1, P2, P3, P4, P5, P6, R> doFirst(Action6<P1, P2, P3, P4, P5, P6> action) {
            return of((p1, p2, p3, p4, p5, p6) -> {
                action.run(p1, p2, p3, p4, p5, p6);
                return call(p1, p2, p3, p4, p5, p6);
            });
        }

        public Func6.X<P1, P2, P3, P4, P5, P6, R> doLast(Action6<P1, P2, P3, P4, P5, P6> action) {
            return of((p1, p2, p3, p4, p5, p6) -> {
                R result = call(p1, p2, p3, p4, p5, p6);
                action.run(p1, p2, p3, p4, p5, p6);
                return result;
            });
        }

        public <R2> Func6.X<P1, P2, P3, P4, P5, P6, R2> to(Func1<? super R, ? extends R2> func) {
            return of((p1, p2, p3, p4, p5, p6) -> func.call(call(p1, p2, p3, p4, p5, p6)));
        }

        public Func6.X<P1, P2, P3, P4, P5, P6, R> run(Action1<? super R> action) {
            return of((p1, p2, p3, p4, p5, p6) -> {
                R result = call(p1, p2, p3, p4, p5, p6);
                action.run(result);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, P6, R> Func6.X<P1, P2, P3, P4, P5, P6, R>
        of(Func6<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? extends R> func) {
            return new Func6.X<P1, P2, P3, P4, P5, P6, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
                    try {
                        return func.call(p1, p2, p3, p4, p5, p6);
                    } catch (Throwable e) {
                        throw Values.wrap(e);
                    }
                }
            };
        }
    }
}
