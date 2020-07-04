package cn.thens.jack.func;

public interface Func0<R> {
    R invoke() throws Throwable;

    abstract class X<R> implements Func0<R> {
        public abstract R invoke();

        private X() {
        }

        public X<R> once() {
            final Once<R> once = Once.create();
            return of(() -> once.call(this));
        }

        public Action0.X action() {
            return Action0.X.of(this::invoke);
        }

        public static <R> X<R> of(Func0<R> func) {
            return new X<R>() {
                @Override
                public R invoke() {
                    try {
                        return func.invoke();
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }
    }
}
