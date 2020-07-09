package cn.thens.jack.func;

public interface Action2<P1, P2> {
    void run(P1 p1, P2 p2) throws Throwable;

    abstract class X<P1, P2>
            implements Action2<P1, P2> {
        public abstract void run(P1 p1, P2 p2);

        private X() {
        }

        public Action1.X<P2> curry(P1 p1) {
            return Action1.X.of((p2) ->
                    run(p1, p2));
        }

        public X<P1, P2> once() {
            final Once<Void> once = Once.create();
            return of((p1, p2) ->
                    once.run(() -> run(p1, p2)));
        }

        public <R> Func2<P1, P2, R> func(R result) {
            return Func2.X.of((p1, p2) -> {
                run(p1, p2);
                return result;
            });
        }

        public static <P1, P2>
        X<P1, P2>
        of(Action2<? super P1, ? super P2> action) {
            return new X<P1, P2>() {
                @Override
                public void run(P1 p1, P2 p2) {
                    try {
                        action.run(p1, p2);
                    } catch (Throwable e) {
                        throw ThrowableWrapper.of(e);
                    }
                }
            };
        }
    }
}
