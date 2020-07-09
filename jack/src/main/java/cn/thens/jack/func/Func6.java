package cn.thens.jack.func;

public interface Func6<P1, P2, P3, P4, P5, P6, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, R> implements Func6<P1, P2, P3, P4, P5, P6, R> {
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

        private X() {
        }

        public Func5.X<P2, P3, P4, P5, P6, R> curry(P1 p1) {
            return Func5.X.of((p2, p3, p4, p5, p6) ->
                    call(p1, p2, p3, p4, p5, p6));
        }

        public X<P1, P2, P3, P4, P5, P6, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6) -> once.call(() -> call(p1, p2, p3, p4, p5, p6)));
        }

        public Action6.X<P1, P2, P3, P4, P5, P6> action() {
            return Action6.X.of(this::call);
        }

        public static <P1, P2, P3, P4, P5, P6, R> X<P1, P2, P3, P4, P5, P6, R>
        of(Func6<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? extends R> func) {
            return new X<P1, P2, P3, P4, P5, P6, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
                    try {
                        return func.call(p1, p2, p3, p4, p5, p6);
                    } catch (Throwable e) {
                        throw ThrowableWrapper.of(e);
                    }
                }
            };
        }
    }
}
