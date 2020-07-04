package cn.thens.jack.func;

public interface Func5<P1, P2, P3, P4, P5, R> {
    R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, R> implements Func5<P1, P2, P3, P4, P5, R> {
        public abstract R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

        private X() {
        }

        public Func4.X<P2, P3, P4, P5, R> curry(P1 p1) {
            return Func4.X.of((p2, p3, p4, p5) ->
                    invoke(p1, p2, p3, p4, p5));
        }

        public X<P1, P2, P3, P4, P5, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5) -> once.call(() -> invoke(p1, p2, p3, p4, p5)));
        }

        public Action5.X<P1, P2, P3, P4, P5> action() {
            return Action5.X.of(this::invoke);
        }

        public static <P1, P2, P3, P4, P5, R> X<P1, P2, P3, P4, P5, R>
        of(Func5<P1, P2, P3, P4, P5, R> func) {
            return new X<P1, P2, P3, P4, P5, R>() {
                @Override
                public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        return func.invoke(p1, p2, p3, p4, p5);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
