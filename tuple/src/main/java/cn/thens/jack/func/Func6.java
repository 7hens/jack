package cn.thens.jack.func;

import cn.thens.jack.util.ThrowableWrapper;

public interface Func6<P1, P2, P3, P4, P5, P6, R> {
    R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) throws Throwable;

    abstract class X<P1, P2, P3, P4, P5, P6, R> implements Func6<P1, P2, P3, P4, P5, P6, R> {
        public abstract R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

        private X() {
        }

        public Func5.X<P2, P3, P4, P5, P6, R> curry(P1 p1) {
            return Func5.X.of((p2, p3, p4, p5, p6) ->
                    invoke(p1, p2, p3, p4, p5, p6));
        }

        public X<P1, P2, P3, P4, P5, P6, R> once() {
            final Once<R> once = Once.create();
            return of((p1, p2, p3, p4, p5, p6) -> once.call(() -> invoke(p1, p2, p3, p4, p5, p6)));
        }

        public Action6.X<P1, P2, P3, P4, P5, P6> action() {
            return Action6.X.of(this::invoke);
        }

        public static <P1, P2, P3, P4, P5, P6, R> X<P1, P2, P3, P4, P5, P6, R>
        of(Func6<P1, P2, P3, P4, P5, P6, R> func) {
            return new X<P1, P2, P3, P4, P5, P6, R>() {
                @Override
                public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
                    try {
                        return func.invoke(p1, p2, p3, p4, p5, p6);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }

        public static X EMPTY = new X() {
            @Override
            public Object invoke(Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
                return null;
            }
        };

        @SuppressWarnings("unchecked")
        public static <P1, P2, P3, P4, P5, P6, R> X<P1, P2, P3, P4, P5, P6, R> empty() {
            return EMPTY;
        }

        public static <P1, P2, P3, P4, P5, P6, R>
        X<P1, P2, P3, P4, P5, P6, R>
        always(final R result) {
            if (result == null) return empty();
            return of((p1, p2, p3, p4, p5, p6) -> result);
        }
    }
}
