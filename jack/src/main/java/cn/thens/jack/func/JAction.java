package cn.thens.jack.func;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
public final class JAction {
    public static void call(T0 func) {
        func.call();
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9> empty() {
        return EMPTY;
    }

    public interface Base {
    }

    public interface T0 extends Base {
        void call();
    }

    public interface T1<P1> extends Base {
        void call(P1 p1);
    }

    public interface T2<P1, P2> extends Base {
        void call(P1 p1, P2 p2);
    }

    public interface T3<P1, P2, P3> extends Base {
        void call(P1 p1, P2 p2, P3 p3);
    }

    public interface T4<P1, P2, P3, P4> extends Base {
        void call(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface T5<P1, P2, P3, P4, P5> extends Base {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface T6<P1, P2, P3, P4, P5, P6> extends Base {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface T7<P1, P2, P3, P4, P5, P6, P7> extends Base {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface T8<P1, P2, P3, P4, P5, P6, P7, P8> extends Base {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface T9<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends Base {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    public interface TN extends Base {
        void call(Object... objects);
    }


    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9> implements
            T0,
            T1<P1>,
            T2<P1, P2>,
            T3<P1, P2, P3>,
            T4<P1, P2, P3, P4>,
            T5<P1, P2, P3, P4, P5>,
            T6<P1, P2, P3, P4, P5, P6>,
            T7<P1, P2, P3, P4, P5, P6, P7>,
            T8<P1, P2, P3, P4, P5, P6, P7, P8>,
            T9<P1, P2, P3, P4, P5, P6, P7, P8, P9>,
            TN {

        private Empty() {
        }

        @Override
        public void call() {
        }

        @Override
        public void call(P1 p1) {
        }

        @Override
        public void call(P1 p1, P2 p2) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3, P4 p4) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        }

        @Override
        public void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
        }

        @Override
        public void call(Object... objects) {
        }
    }
}
