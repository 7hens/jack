package cn.thens.jack;

@SuppressWarnings({"unused", "WeakerAccess", "unchecked"})
public final class JFunc {
    private JFunc() {
    }

    public static void run(A0 func) {
        func.run();
    }

    public static <R> R call(F0<R> func) {
        return func.call();
    }

    public interface A {
    }

    public interface A0 extends A {
        void run();
    }

    public interface A1<P1> extends A {
        void run(P1 p1);
    }

    public interface A2<P1, P2> extends A {
        void run(P1 p1, P2 p2);
    }

    public interface A3<P1, P2, P3> extends A {
        void run(P1 p1, P2 p2, P3 p3);
    }

    public interface A4<P1, P2, P3, P4> extends A {
        void run(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    public interface A5<P1, P2, P3, P4, P5> extends A {
        void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    public interface A6<P1, P2, P3, P4, P5, P6> extends A {
        void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    public interface A7<P1, P2, P3, P4, P5, P6, P7> extends A {
        void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    public interface A8<P1, P2, P3, P4, P5, P6, P7, P8> extends A {
        void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    public interface A9<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends A {
        void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    public interface An extends A {
        void run(Object... objects);
    }

    public interface F<R> extends A {
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

    public interface Fn<R> extends F<R> {
        R call(Object... objects);
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> empty() {
        return EMPTY;
    }

    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> implements
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
            An,
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
            Fn<R> {

        private Empty() {
        }

        @Override
        public void run() {
        }

        @Override
        public void run(P1 p1) {
        }

        @Override
        public void run(P1 p1, P2 p2) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        }

        @Override
        public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
        }

        @Override
        public void run(Object... objects) {
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
