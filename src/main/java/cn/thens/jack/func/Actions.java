package cn.thens.jack.func;

@SuppressWarnings("unchecked")
public class Actions<P1, P2, P3, P4, P5, P6, P7, P8, P9> implements
        Action0,
        Action1<P1>,
        Action2<P1, P2>,
        Action3<P1, P2, P3>,
        Action4<P1, P2, P3, P4>,
        Action5<P1, P2, P3, P4, P5>,
        Action6<P1, P2, P3, P4, P5, P6>,
        Action7<P1, P2, P3, P4, P5, P6, P7>,
        Action8<P1, P2, P3, P4, P5, P6, P7, P8>,
        Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {

    @SuppressWarnings("rawtypes")
    private static Actions EMPTY = new Actions();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Actions<P1, P2, P3, P4, P5, P6, P7, P8, P9> empty() {
        return EMPTY;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Actions<P1, P2, P3, P4, P5, P6, P7, P8, P9> from(Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> action) {
        Action9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9> actionX = of(action);
        return new Actions<P1, P2, P3, P4, P5, P6, P7, P8, P9>() {
            @Override
            public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
                actionX.run(p1, p2, p3, p4, p5, p6, p7, p8, p9);
            }
        };
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Actions<P1, P2, P3, P4, P5, P6, P7, P8, P9> error(Throwable error) {
        return from((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> {
            throw error;
        });
    }

    public static void run(Action0 action) {
        of(action).run();
    }

    public static Action0.X of(Action0 action) {
        return Action0.X.of(action);
    }

    public static <P1> Action1.X<P1> of(Action1<? super P1> action) {
        return Action1.X.of(action);
    }

    public static <P1, P2> Action2.X<P1, P2> of(Action2<? super P1, ? super P2> action) {
        return Action2.X.of(action);
    }

    public static <P1, P2, P3>
    Action3.X<P1, P2, P3>
    of(Action3<? super P1, ? super P2, ? super P3> action) {
        return Action3.X.of(action);
    }

    public static <P1, P2, P3, P4>
    Action4.X<P1, P2, P3, P4>
    of(Action4<? super P1, ? super P2, ? super P3, ? super P4> action) {
        return Action4.X.of(action);
    }

    public static <P1, P2, P3, P4, P5>
    Action5.X<P1, P2, P3, P4, P5>
    of(Action5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5> action) {
        return Action5.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6>
    Action6.X<P1, P2, P3, P4, P5, P6>
    of(Action6<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6> action) {
        return Action6.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6, P7>
    Action7.X<P1, P2, P3, P4, P5, P6, P7>
    of(Action7<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7> action) {
        return Action7.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8>
    Action8.X<P1, P2, P3, P4, P5, P6, P7, P8>
    of(Action8<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8> action) {
        return Action8.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Action9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9>
    of(Action9<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8, ? super P9> action) {
        return Action9.X.of(action);
    }

    public Action1.X<P1> x1() {
        return Action1.X.of(this);
    }

    public Action2.X<P1, P2> x2() {
        return Action2.X.of(this);
    }

    public Action3.X<P1, P2, P3> x3() {
        return Action3.X.of(this);
    }

    public Action4.X<P1, P2, P3, P4> x4() {
        return Action4.X.of(this);
    }

    public Action5.X<P1, P2, P3, P4, P5> x5() {
        return Action5.X.of(this);
    }

    public Action6.X<P1, P2, P3, P4, P5, P6> x6() {
        return Action6.X.of(this);
    }

    public Action7.X<P1, P2, P3, P4, P5, P6, P7> x7() {
        return Action7.X.of(this);
    }

    public Action8.X<P1, P2, P3, P4, P5, P6, P7, P8> x8() {
        return Action8.X.of(this);
    }

    public Action9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9> x9() {
        return Action9.X.of(this);
    }

    private Actions() {
    }

    @Override
    public void run() {
        run(null, null, null, null, null, null, null, null, null);
    }

    @Override
    public void run(P1 p1) {
        run(p1, null, null, null, null, null, null, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2) {
        run(p1, p2, null, null, null, null, null, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3) {
        run(p1, p2, p3, null, null, null, null, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3, P4 p4) {
        run(p1, p2, p3, p4, null, null, null, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        run(p1, p2, p3, p4, p5, null, null, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        run(p1, p2, p3, p4, p5, p6, null, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        run(p1, p2, p3, p4, p5, p6, p7, null, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        run(p1, p2, p3, p4, p5, p6, p7, p8, null);
    }

    @Override
    public void run(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
    }
}
