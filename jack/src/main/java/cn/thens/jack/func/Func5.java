package cn.thens.jack.func;

public interface Func5<P1, P2, P3, P4, P5, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, R> implements Func5<P1, P2, P3, P4, P5, R> {
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

        private X() {
        }

        public Func4.X<P2, P3, P4, P5, R> curry(P1 p1) {
            return Func4.X.of((p2, p3, p4, p5) ->
                    call(p1, p2, p3, p4, p5));
        }

        public X<P1, P2, P3, P4, P5, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5) -> once.call(() -> call(p1, p2, p3, p4, p5)));
        }

        public Action5.X<P1, P2, P3, P4, P5> action() {
            return Action5.X.of(this::call);
        }

        public Func5.X<P1, P2, P3, P4, P5, R> doFirst(Action5<P1, P2, P3, P4, P5> action) {
            return of((p1, p2, p3, p4, p5) -> {
                action.run(p1, p2, p3, p4, p5);
                return call(p1, p2, p3, p4, p5);
            });
        }

        public Func5.X<P1, P2, P3, P4, P5, R> doLast(Action5<P1, P2, P3, P4, P5> action) {
            return of((p1, p2, p3, p4, p5) -> {
                R result = call(p1, p2, p3, p4, p5);
                action.run(p1, p2, p3, p4, p5);
                return result;
            });
        }

        public static <P1, P2, P3, P4, P5, R> X<P1, P2, P3, P4, P5, R>
        of(Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> func) {
            return new X<P1, P2, P3, P4, P5, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
                    try {
                        return func.call(p1, p2, p3, p4, p5);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
