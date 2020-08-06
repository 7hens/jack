package cn.thens.jack.func;

public interface Func4<P1, P2, P3, P4, R> {
    R call(P1 p1, P2 p2, P3 p3, P4 p4) throws Throwable;

    abstract class X<P1, P2, P3, P4, R> implements Func4<P1, P2, P3, P4, R>, Action4<P1, P2, P3, P4> {
        @Override
        public abstract R call(P1 p1, P2 p2, P3 p3, P4 p4);

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4) {
            call(p1, p2, p3, p4);
        }

        private X() {
        }

        public Func3.X<P2, P3, P4, R> curry(P1 p1) {
            return Func3.X.of((p2, p3, p4) ->
                    call(p1, p2, p3, p4));
        }

        public Func4.X<P1, P2, P3, P4, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4) -> once.call(() -> call(p1, p2, p3, p4)));
        }

        public Action4.X<P1, P2, P3, P4> action() {
            return Action4.X.of(this);
        }

        public Func4.X<P1, P2, P3, P4, R> doFirst(Action4<P1, P2, P3, P4> action) {
            return of((p1, p2, p3, p4) -> {
                action.run(p1, p2, p3, p4);
                return call(p1, p2, p3, p4);
            });
        }

        public Func4.X<P1, P2, P3, P4, R> doLast(Action4<P1, P2, P3, P4> action) {
            return of((p1, p2, p3, p4) -> {
                R result = call(p1, p2, p3, p4);
                action.run(p1, p2, p3, p4);
                return result;
            });
        }

        public <R2> Func4.X<P1, P2, P3, P4, R2> to(Func1<? super R, ? extends R2> func) {
            return of((p1, p2, p3, p4) -> func.call(call(p1, p2, p3, p4)));
        }

        public Func4.X<P1, P2, P3, P4, R> run(Action1<? super R> action) {
            return of((p1, p2, p3, p4) -> {
                R result = call(p1, p2, p3, p4);
                action.run(result);
                return result;
            });
        }

        public static <P1, P2, P3, P4, R> Func4.X<P1, P2, P3, P4, R>
        of(Func4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> func) {
            return new Func4.X<P1, P2, P3, P4, R>() {
                @Override
                public R call(P1 p1, P2 p2, P3 p3, P4 p4) {
                    try {
                        return func.call(p1, p2, p3, p4);
                    } catch (Throwable e) {
                        throw Values.wrap(e);
                    }
                }
            };
        }
    }
}
