package cn.thens.jack.func;

public interface Func2<P1, P2, R> {
    R invoke(P1 p1, P2 p2) throws Throwable;

    abstract class X<P1, P2, R> implements Func2<P1, P2, R> {
        public abstract R invoke(P1 p1, P2 p2);

        private X() {
        }

        public Func1.X<P2, R> curry(P1 p1) {
            return Funcs.of((Func1<P2, R>) (p2) ->
                    invoke(p1, p2));
        }

        public X<P1, P2, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2) -> once.call(() -> invoke(p1, p2)));
        }

        public Action2.X<P1, P2> action() {
            return Action2.X.of(this::invoke);
        }

        public static <P1, P2, R> X<P1, P2, R> of(Func2<? super P1, ? super P2, ? extends R> func) {
            return new X<P1, P2, R>() {
                @Override
                public R invoke(P1 p1, P2 p2) {
                    try {
                        return func.invoke(p1, p2);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
