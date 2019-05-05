package cn.thens.jack.func;

@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
public final class JFunc {
    private JFunc() {
    }

    public static <R> R call(T0<R> func) {
        return func.call();
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> empty() {
        return EMPTY;
    }

    public interface Base<R> extends JAction.Base {
    }

    public interface T0<R> extends Base<R> {
        R call();
    }

    public interface T1<P1, R> extends Base<R> {
        R call(P1 p1);
    }

    public interface T2<P1, P2, R> extends Base<R> {
        R call(P1 p1, P2 p2);
    }

    public interface T3<P1, P2, P3, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3);
    }

    public interface T4<P1, P2, P3, P4, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface T5<P1, P2, P3, P4, P5, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface T6<P1, P2, P3, P4, P5, P6, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface T7<P1, P2, P3, P4, P5, P6, P7, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface T8<P1, P2, P3, P4, P5, P6, P7, P8, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface T9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> extends Base<R> {
        R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    public interface TN<R> extends Base<R> {
        R call(Object... objects);
    }

    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> implements
            T0<R>,
            T1<P1, R>,
            T2<P1, P2, R>,
            T3<P1, P2, P3, R>,
            T4<P1, P2, P3, P4, R>,
            T5<P1, P2, P3, P4, P5, R>,
            T6<P1, P2, P3, P4, P5, P6, R>,
            T7<P1, P2, P3, P4, P5, P6, P7, R>,
            T8<P1, P2, P3, P4, P5, P6, P7, P8, R>,
            T9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>,
            TN<R> {

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
