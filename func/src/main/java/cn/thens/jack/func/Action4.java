package cn.thens.jack.func;

public interface Action4<P1, P2, P3, P4> {
    void run(P1 p1, P2 p2, P3 p3, P4 p4) throws Throwable;

    abstract class X<P1, P2, P3, P4>
            implements Action4<P1, P2, P3, P4> {
        public abstract void run(P1 p1, P2 p2, P3 p3, P4 p4);

        private X() {
        }

        public Action3.X<P2, P3, P4> curry(P1 p1) {
            return Action3.X.of((p2, p3, p4) ->
                    run(p1, p2, p3, p4));
        }

        public X<P1, P2, P3, P4> once() {
            final Once<Void> once = Once.create();
            return of((p1, p2, p3, p4) ->
                    once.call(() -> run(p1, p2, p3, p4)));
        }

        public <R> Func4.X<P1, P2, P3, P4, R> func(R result) {
            return Func4.X.of((p1, p2, p3, p4) -> {
                run(p1, p2, p3, p4);
                return result;
            });
        }

        public static <P1, P2, P3, P4>
        X<P1, P2, P3, P4>
        of(Action4<P1, P2, P3, P4> action) {
            return new X<P1, P2, P3, P4>() {
                @Override
                public void run(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        action.run(p1, p2, p3, p4);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
