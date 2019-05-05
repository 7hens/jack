package cn.thens.jack.func;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
public final class JAction {
    public static void call(A0 func) {
        func.call();
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9> empty() {
        return EMPTY;
    }

    public interface A {
    }

    public interface A0 extends A {
        void call();
    }

    public interface A1<P1> extends A {
        void call(P1 p1);
    }

    public interface A2<P1, P2> extends A {
        void call(P1 p1, P2 p2);
    }

    public interface A3<P1, P2, P3> extends A {
        void call(P1 p1, P2 p2, P3 p3);
    }

    public interface A4<P1, P2, P3, P4> extends A {
        void call(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface A5<P1, P2, P3, P4, P5> extends A {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface A6<P1, P2, P3, P4, P5, P6> extends A {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface A7<P1, P2, P3, P4, P5, P6, P7> extends A {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface A8<P1, P2, P3, P4, P5, P6, P7, P8> extends A {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface A9<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends A {
        void call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    public interface AN extends A {
        void call(Object... objects);
    }


    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9> implements
            A0,
            A1<P1>,
            A2<P1, P2>,
            A3<P1, P2, P3>,
            A4<P1, P2, P3, P4>,
            A5<P1, P2, P3, P4, P5>,
            A6<P1, P2, P3, P4, P5, P6>,
            A7<P1, P2, P3, P4, P5, P6, P7>,
            A8<P1, P2, P3, P4, P5, P6, P7, P8>,
            A9<P1, P2, P3, P4, P5, P6, P7, P8, P9>,
            AN {

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
