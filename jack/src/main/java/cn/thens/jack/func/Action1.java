package cn.thens.jack.func;

public interface Action1<P1> {
    void run(P1 p1) throws Throwable;

    abstract class X<P1>
            implements Action1<P1> {
        public abstract void run(P1 p1);

        private X() {
        }

        public Action0.X curry(P1 p1) {
            return Action0.X.of(() ->
                    run(p1));
        }

        public X<P1> once() {
            final Once<Void> once = Once.create();
            return of((p1) ->
                    once.run(() -> run(p1)));
        }

        public <R> Func1.X<P1, R> func(R result) {
            return Func1.X.of((p1) -> {
                run(p1);
                return result;
            });
        }

        public static <P1>
        X<P1>
        of(Action1<? super P1> action) {
            return new X<P1>() {
                @Override
                public void run(P1 p1) {
                    try {
                        action.run(p1);
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
