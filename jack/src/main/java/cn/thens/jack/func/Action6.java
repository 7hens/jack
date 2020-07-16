package cn.thens.jack.func;

public interface Action6<P1, P2, P3, P4, P5, P6> {
    void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6>
            implements Action6<P1, P2, P3, P4, P5, P6> {
        public abstract void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

        private X() {
        }

        public Action5.X<P2, P3, P4, P5, P6> curry(P1 p1) {
            return Action5.X.of((p2, p3, p4, p5, p6) ->
                    run(p1, p2, p3, p4, p5, p6));
        }

        public X<P1, P2, P3, P4, P5, P6> once() {
            final Once<Void> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6) ->
                    once.run(() -> run(p1, p2, p3, p4, p5, p6)));
        }

        public <R> Func6.X<P1, P2, P3, P4, P5, P6, R> func(R result) {
            return Func6.X.of((p1, p2, p3, p4, p5, p6) -> {
                run(p1, p2, p3, p4, p5, p6);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, P6>
        X<P1, P2, P3, P4, P5, P6>
        of(Action6<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6> action) {
            return new X<P1, P2, P3, P4, P5, P6>() {
                @Override
                public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
                    try {
                        action.run(p1, p2, p3, p4, p5, p6);
                    } catch (Throwable e) {
                        throw Exceptions.wrap(e);
                    }
                }
            };
        }
    }
}
