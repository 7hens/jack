package cn.thens.jack.func;

public interface Func2<P1, P2, R> {
    R call(P1 p1, P2 p2) throws Throwable;

    abstract class X<P1, P2, R> implements Func2<P1, P2, R> {
        public abstract R call(P1 p1, P2 p2);

        private X() {
        }

        public Func1.X<P2, R> curry(P1 p1) {
            return Funcs.of((Func1<P2, R>) (p2) ->
                    call(p1, p2));
        }

        public X<P1, P2, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2) -> once.call(() -> call(p1, p2)));
        }

        public Action2.X<P1, P2> action() {
            return Action2.X.of(this::call);
        }

        public Func2.X<P1, P2, R> doFirst(Action2<P1, P2> action) {
            return of((p1, p2) -> {
                action.run(p1, p2);
                return call(p1, p2);
            });
        }

        public Func2.X<P1, P2, R> doLast(Action2<P1, P2> action) {
            return of((p1, p2) -> {
                R result = call(p1, p2);
                action.run(p1, p2);
                return result;
            });
        }

        public <R2> Func2.X<P1, P2, R2> to(Func1<? super R, ? extends R2> func) {
            return of((p1, p2) -> func.call(call(p1, p2)));
        }

        public Func2.X<P1, P2, R> run(Action1<? super R> action) {
            return of((p1, p2) -> {
                R result = call(p1, p2);
                action.run(result);
                return result;
            });
        }

        public static <P1, P2, R> X<P1, P2, R> of(Func2<? super P1, ? super P2, ? extends R> func) {
            return new X<P1, P2, R>() {
                @Override
                public R call(P1 p1, P2 p2) {
                    try {
                        return func.call(p1, p2);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
