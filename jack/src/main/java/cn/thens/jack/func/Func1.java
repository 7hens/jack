package cn.thens.jack.func;

public interface Func1<P1, R> {
    R call(P1 p1) throws Throwable;

    abstract class X<P1, R> implements Func1<P1, R> {
        public abstract R call(P1 p1);

        private X() {
        }

        public Func0.X<R> curry(P1 p1) {
            return Funcs.of(() ->
                    call(p1));
        }

        public X<P1, R> once() {
            final Once<R> once = Once.create();
            return of((p1) -> once.call(() -> call(p1)));
        }

        public Action1.X<P1> action() {
            return Action1.X.of(this::call);
        }

        public Func1.X<P1, R> doFirst(Action1<P1> action) {
            return of((p1) -> {
                action.run(p1);
                return call(p1);
            });
        }

        public Func1.X<P1, R> doLast(Action1<P1> action) {
            return of((p1) -> {
                R result = call(p1);
                action.run(p1);
                return result;
            });
        }

        public <R2> Func1.X<P1, R2> to(Func1<? super R, ? extends R2> func) {
            return of(p1 -> func.call(call(p1)));
        }

        public Func1.X<P1, R> run(Action1<? super R> action) {
            return of(p1 -> {
                R result = call(p1);
                action.run(result);
                return result;
            } );
        }

        public static <P1, R> X<P1, R> of(Func1<? super P1, ? extends R> func) {
            return new X<P1, R>() {
                @Override
                public R call(P1 p1) {
                    try {
                        return func.call(p1);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
