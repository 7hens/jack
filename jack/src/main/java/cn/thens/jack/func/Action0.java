package cn.thens.jack.func;

public interface Action0 {
    void run() throws Throwable;

    abstract class X implements Action0 {
        public abstract void run();

        private X() {
        }

        public X once() {
            final Once<Void> once = Once.create();
            return of(() -> once.run(this));
        }

        public <R> Func0.X<R> func(R result) {
            return Func0.X.of(() -> {
                run();
                return result;
            });
        }

        public static X of(Action0 action) {
            return new X() {
                @Override
                public void run() {
                    try {
                        action.run();
                    } catch (Throwable e) {
                        throw Things.wrap(e);
                    }
                }
            };
        }
    }
}
