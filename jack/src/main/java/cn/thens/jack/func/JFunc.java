package cn.thens.jack.func;

@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
public final class JFunc {
    private JFunc() {
    }

    public static <R> R call(F0<R> func) {
        return func.call();
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> empty() {
        return EMPTY;
    }

    public interface F<R> extends JAction.A {
    }

    public interface F0<R> extends F<R> {
        R call();
    }

    public interface F1<P1, R> extends F<R> {
        R call(P1 p1);
    }

    public interface F2<P1, P2, R> extends F<R> {
        R call(P1 p1, P2 p2);
    }

    public interface F3<P1, P2, P3, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3);
    }

    public interface F4<P1, P2, P3, P4, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface F5<P1, P2, P3, P4, P5, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface F6<P1, P2, P3, P4, P5, P6, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface F7<P1, P2, P3, P4, P5, P6, P7, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface F8<P1, P2, P3, P4, P5, P6, P7, P8, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface F9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> extends F<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    public interface FN<R> extends F<R> {
        R call(Object... objects);
    }

    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> implements
            F0<R>,
            F1<P1, R>,
            F2<P1, P2, R>,
            F3<P1, P2, P3, R>,
            F4<P1, P2, P3, P4, R>,
            F5<P1, P2, P3, P4, P5, R>,
            F6<P1, P2, P3, P4, P5, P6, R>,
            F7<P1, P2, P3, P4, P5, P6, P7, R>,
            F8<P1, P2, P3, P4, P5, P6, P7, P8, R>,
            F9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>,
            FN<R> {

        private Empty() {
        }

        @Override
        public R call() {
            return null;
        }

        @Override
        public R call(P1 p1) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3, P4 p4) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
            return null;
        }

        @Override
        public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
            return null;
        }

        @Override
        public R call(Object... objects) {
            return null;
        }
    }
}
