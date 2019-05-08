package cn.thens.jack.func;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused", "unchecked"})
public final class JAction {
    private JAction() throws InstantiationException {
        throw new InstantiationException();
    }

    public static void call(JAction0 func) {
        func.invoke();
    }

    private static Empty EMPTY = new Empty();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9> empty() {
        return EMPTY;
    }


    public static final class Empty<P1, P2, P3, P4, P5, P6, P7, P8, P9> implements
            JAction0,
            JAction1<P1>,
            JAction2<P1, P2>,
            JAction3<P1, P2, P3>,
            JAction4<P1, P2, P3, P4>,
            JAction5<P1, P2, P3, P4, P5>,
            JAction6<P1, P2, P3, P4, P5, P6>,
            JAction7<P1, P2, P3, P4, P5, P6, P7>,
            JAction8<P1, P2, P3, P4, P5, P6, P7, P8>,
            JAction9<P1, P2, P3, P4, P5, P6, P7, P8, P9>,
            JActionN {

        private Empty() {
        }

        @Override
        public void invoke() {
        }

        @Override
        public void invoke(P1 p1) {
        }

        @Override
        public void invoke(P1 p1, P2 p2) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3, P4 p4) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        }

        @Override
        public void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
        }

        @Override
        public void invoke(Object... objects) {
        }
    }
}
