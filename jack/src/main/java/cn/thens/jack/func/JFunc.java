package cn.thens.jack.func;

@SuppressWarnings({"unused", "unchecked"})
public final class JFunc {
    private JFunc() throws InstantiationException {
        throw new InstantiationException();
    }

    public static <R> R call(JFunc0<R> func) {
        return func.invoke();
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> empty() {
        return EMPTY;
    }

    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> implements
            JFunc0<R>,
            JFunc1<P1, R>,
            JFunc2<P1, P2, R>,
            JFunc3<P1, P2, P3, R>,
            JFunc4<P1, P2, P3, P4, R>,
            JFunc5<P1, P2, P3, P4, P5, R>,
            JFunc6<P1, P2, P3, P4, P5, P6, R>,
            JFunc7<P1, P2, P3, P4, P5, P6, P7, R>,
            JFunc8<P1, P2, P3, P4, P5, P6, P7, P8, R>,
            JFunc9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>,
            JFuncN<R> {

        private Empty() {
        }

        @Override
        public R invoke() {
            return null;
        }

        @Override
        public R invoke(P1 p1) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3, P4 p4) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
            return null;
        }

        @Override
        public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
            return null;
        }

        @Override
        public R invoke(Object... objects) {
            return null;
        }
    }
}
