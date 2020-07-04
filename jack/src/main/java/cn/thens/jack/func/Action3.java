package cn.thens.jack.func;

import cn.thens.jack.util.ThrowableWrapper;

public interface Action3<P1, P2, P3> {
    void run(P1 p1, P2 p2, P3 p3) throws Throwable;

    abstract class X<P1, P2, P3>
            implements Action3<P1, P2, P3> {
        public abstract void run(P1 p1, P2 p2, P3 p3);

        private X() {
        }

        public Action2.X<P2, P3> curry(P1 p1) {
            return Action2.X.of((p2, p3) ->
                    run(p1, p2, p3));
        }

        public X<P1, P2, P3> once() {
            final Once<Void> once = Once.create();
            return of((p1, p2, p3) ->
                    once.call(() -> run(p1, p2, p3)));
        }

        public <R> Func3.X<P1, P2, P3, R> func(R result) {
            return Func3.X.of((p1, p2, p3) -> {
                run(p1, p2, p3);
                return result;
            });
        }

        public static <P1, P2, P3>
        X<P1, P2, P3>
        of(Action3<P1, P2, P3> action) {
            return new X<P1, P2, P3>() {
                @Override
                public void run(P1 p1, P2 p2, P3 p3) {
                    try {
                        action.run(p1, p2, p3);
                    } catch (Throwable e) {
                        throw new ThrowableWrapper(e);
                    }
                }
            };
        }

        private static final X EMPTY = new X() {
            @Override
            public void run(Object p1, Object p2, Object p3) {
            }
        };

        @SuppressWarnings("unchecked")
        public static <P1, P2, P3>
        X<P1, P2, P3>
        empty() {
            return EMPTY;
        }
    }
}