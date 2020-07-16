package cn.thens.jack.func;

public interface Func0<R> {
    R call() throws Throwable;

    abstract class X<R> implements Func0<R> {
        public abstract R call();

        private X() {
        }

        public X<R> once() {
            final Once<R> once = Once.create();
            return of(() -> once.call(this));
        }

        public Action0.X action() {
            return Action0.X.of(this::call);
        }

        public X<R> doFirst(Action0 action) {
            return of(() -> {
                action.run();
                return call();
            });
        }

        public X<R> doLast(Action0 action) {
            return of(() -> {
                R result = call();
                action.run();
                return result;
            });
        }

        public static <R> X<R> of(Func0<? extends R> func) {
            return new X<R>() {
                @Override
                public R call() {
                    try {
                        return func.call();
                    } catch (Throwable e) {
                        throw Exceptions.wrap(e);
                    }
                }
            };
        }
    }
}
