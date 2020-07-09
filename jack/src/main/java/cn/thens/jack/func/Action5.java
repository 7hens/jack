package cn.thens.jack.func;

public interface Action5<P1, P2, P3, P4, P5> {
    void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5>
            implements Action5<P1, P2, P3, P4, P5> {
        public abstract void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

        private X() {
        }

        public Action4.X<P2, P3, P4, P5> curry(P1 p1) {
            return Action4.X.of((p2, p3, p4, p5) ->
                    run(p1, p2, p3, p4, p5));
        }

        public X<P1, P2, P3, P4, P5> once() {
            final Once<Void> once = Once.create();
            return of((p1, p2, p3, p4, p5) ->
                    once.run(() -> run(p1, p2, p3, p4, p5)));
        }

        public <R> Func5.X<P1, P2, P3, P4, P5, R> func(R result) {
            return Func5.X.of((p1, p2, p3, p4, p5) -> {
                run(p1, p2, p3, p4, p5);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5>
        X<P1, P2, P3, P4, P5>
        of(Action5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5> action) {
            return new X<P1, P2, P3, P4, P5>() {
                @Override
                public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        action.run(p1, p2, p3, p4, p5);
                    } catch (Throwable e) {
                        throw ThrowableWrapper.of(e);
                    }
                }
            };
        }
    }
}
