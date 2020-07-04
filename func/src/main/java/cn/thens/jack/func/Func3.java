package cn.thens.jack.func;

public interface Func3<P1, P2, P3, R> {
    R invoke(P1 p1, P2 p2, P3 p3) throws Throwable;

    abstract class X<P1, P2, P3, R> implements Func3<P1, P2, P3, R> {
        public abstract R invoke(P1 p1, P2 p2, P3 p3);

        private X() {
        }

        public Func2.X<P2, P3, R> curry(P1 p1) {
            return Func2.X.of((p2, p3) ->
                    invoke(p1, p2, p3));
        }

        public X<P1, P2, P3, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3) -> once.call(() -> invoke(p1, p2, p3)));
        }

        public Action3.X<P1, P2, P3> action() {
            return Action3.X.of(this::invoke);
        }

        public static <P1, P2, P3, R> X<P1, P2, P3, R> of(Func3<P1, P2, P3, R> func) {
            return new X<P1, P2, P3, R>() {
                @Override
                public R invoke(P1 p1, P2 p2, P3 p3) {
                    try {
                        return func.invoke(p1, p2, p3);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }

        public static X EMPTY = new X() {
            @Override
            public Object invoke(Object p1, Object p2, Object p3) {
                return null;
            }
        };

        @SuppressWarnings("unchecked")
        public static <P1, P2, P3, R> X<P1, P2, P3, R> empty() {
            return EMPTY;
        }

        public static <P1, P2, P3, R> X<P1, P2, P3, R> always(final R result) {
            if (result == null) return empty();
            return of((p1, p2, p3) -> result);
        }
    }
}
