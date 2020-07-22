package cn.thens.jack.func;

public interface Func7<P1, P2, P3, P4, P5, P6, P7, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, P7, R> implements Func7<P1, P2, P3, P4, P5, P6, P7, R> {
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

        private X() {
        }

        public Func6.X<P2, P3, P4, P5, P6, P7, R> curry(P1 p1) {
            return Func6.X.of((p2, p3, p4, p5, p6, p7) ->
                    call(p1, p2, p3, p4, p5, p6, p7));
        }

        public X<P1, P2, P3, P4, P5, P6, P7, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6, p7) ->
                    once.call(() -> call(p1, p2, p3, p4, p5, p6, p7)));
        }

        public Action7.X<P1, P2, P3, P4, P5, P6, P7> action() {
            return Action7.X.of(this::call);
        }

        public Func7.X<P1, P2, P3, P4, P5, P6, P7, R>
        doFirst(Action7<P1, P2, P3, P4, P5, P6, P7> action) {
            return of((p1, p2, p3, p4, p5, p6, p7) -> {
                action.run(p1, p2, p3, p4, p5, p6, p7);
                return call(p1, p2, p3, p4, p5, p6, p7);
            });
        }

        public Func7.X<P1, P2, P3, P4, P5, P6, P7, R>
        doLast(Action7<P1, P2, P3, P4, P5, P6, P7> action) {
            return of((p1, p2, p3, p4, p5, p6, p7) -> {
                R result = call(p1, p2, p3, p4, p5, p6, p7);
                action.run(p1, p2, p3, p4, p5, p6, p7);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, P6, P7, R>
        X<P1, P2, P3, P4, P5, P6, P7, R>
        of(Func7<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? extends R> func) {
            return new X<P1, P2, P3, P4, P5, P6, P7, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
                    try {
                        return func.call(p1, p2, p3, p4, p5, p6, p7);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
