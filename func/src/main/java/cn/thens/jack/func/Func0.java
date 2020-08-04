package cn.thens.jack.func;

public interface Func0<R> {
    R call() throws Throwable;

    abstract class X<R> implements Func0<R>, Action0 {
        @Override
        public abstract R call();

        @Override
        public void run() {
            call();
        }

        private X() {
        }

        public Func0.X<R> once() {
            final Once<R> once = Once.create();
            return of(() -> once.call(this));
        }

        public Action0.X action() {
            return Action0.X.of(this);
        }

        public Func0.X<R> doFirst(Action0 action) {
            return of(() -> {
                action.run();
                return call();
            });
        }

        public Func0.X<R> doLast(Action0 action) {
            return of(() -> {
                R result = call();
                action.run();
                return result;
            });
        }

        public <R2> Func0.X<R2> to(Func1<? super R, ? extends R2> func) {
            return of(() -> func.call(call()));
        }

        public Func0.X<R> run(Action1<? super R> action) {
            return of(() -> {
                R result = call();
                action.run(result);
                return result;
            });
        }

        public static <R> Func0.X<R> of(Func0<? extends R> func) {
            return new Func0.X<R>() {
                @Override
                public R call() {
                    try {
                        return func.call();
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
