package cn.thens.jack.func;

public interface Func4<P1, P2, P3, P4, R> {
    R invoke(P1 p1, P2 p2, P3 p3, P4 p4) throws Throwable;

    abstract class X<P1, P2, P3, P4, R> implements Func4<P1, P2, P3, P4, R> {
        public abstract R invoke(P1 p1, P2 p2, P3 p3, P4 p4);

        private X() {
        }

        public Func3.X<P2, P3, P4, R> curry(P1 p1) {
            return Func3.X.of((p2, p3, p4) ->
                    invoke(p1, p2, p3, p4));
        }

        public X<P1, P2, P3, P4, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4) -> once.call(() -> invoke(p1, p2, p3, p4)));
        }

        public Action4.X<P1, P2, P3, P4> action() {
            return Action4.X.of(this::invoke);
        }

        public static <P1, P2, P3, P4, R> X<P1, P2, P3, P4, R>
        of(Func4<P1, P2, P3, P4, R> func) {
            return new X<P1, P2, P3, P4, R>() {
                @Override
                public R invoke(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        return func.invoke(p1, p2, p3, p4);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
