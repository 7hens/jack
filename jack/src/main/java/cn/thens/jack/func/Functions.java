package cn.thens.jack.func;

import cn.thens.jack.util.ThrowableWrapper;

@SuppressWarnings("unchecked")
public class Functions<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> implements
        Action0,
        Action1<P1>,
        Action2<P1, P2>,
        Action3<P1, P2, P3>,
        Action4<P1, P2, P3, P4>,
        Action5<P1, P2, P3, P4, P5>,
        Action6<P1, P2, P3, P4, P5, P6>,
        Action7<P1, P2, P3, P4, P5, P6, P7>,
        Action8<P1, P2, P3, P4, P5, P6, P7, P8>,
        Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9>,
        ActionN,
        Func0<R>,
        Func1<P1, R>,
        Func2<P1, P2, R>,
        Func3<P1, P2, P3, R>,
        Func4<P1, P2, P3, P4, R>,
        Func5<P1, P2, P3, P4, P5, R>,
        Func6<P1, P2, P3, P4, P5, P6, R>,
        Func7<P1, P2, P3, P4, P5, P6, P7, R>,
        Func8<P1, P2, P3, P4, P5, P6, P7, P8, R>,
        Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>,
        FuncN<R> {

    private static Functions EMPTY = new Functions(null);

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Functions<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> empty() {
        return EMPTY;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Functions<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> always(R result) {
        return result != null ? new Functions<>(result) : empty();
    }

    public static Action0.X of(Action0 action) {
        return Action0.X.of(action);
    }

    public static <P1>
    Action1.X<P1>
    of(Action1<P1> action) {
        return Action1.X.of(action);
    }

    public static <P1, P2>
    Action2.X<P1, P2>
    of(Action2<P1, P2> action) {
        return Action2.X.of(action);
    }

    public static <P1, P2, P3>
    Action3.X<P1, P2, P3>
    of(Action3<P1, P2, P3> action) {
        return Action3.X.of(action);
    }

    public static <P1, P2, P3, P4>
    Action4.X<P1, P2, P3, P4>
    of(Action4<P1, P2, P3, P4> action) {
        return Action4.X.of(action);
    }

    public static <P1, P2, P3, P4, P5>
    Action5.X<P1, P2, P3, P4, P5>
    of(Action5<P1, P2, P3, P4, P5> action) {
        return Action5.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6>
    Action6.X<P1, P2, P3, P4, P5, P6>
    of(Action6<P1, P2, P3, P4, P5, P6> action) {
        return Action6.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6, P7>
    Action7.X<P1, P2, P3, P4, P5, P6, P7>
    of(Action7<P1, P2, P3, P4, P5, P6, P7> action) {
        return Action7.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8>
    Action8.X<P1, P2, P3, P4, P5, P6, P7, P8>
    of(Action8<P1, P2, P3, P4, P5, P6, P7, P8> action) {
        return Action8.X.of(action);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Action9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9>
    of(Action9<P1, P2, P3, P4, P5, P6, P7, P8, P9> action) {
        return Action9.X.of(action);
    }

    public static <R> Func0.X<R>
    of(Func0<R> func) {
        return Func0.X.of(func);
    }

    public static <P1, R> Func1.X<P1, R>
    of(Func1<P1, R> func) {
        return Func1.X.of(func);
    }

    public static <P1, P2, R> Func2.X<P1, P2, R>
    of(Func2<P1, P2, R> func) {
        return Func2.X.of(func);
    }

    public static <P1, P2, P3, R> Func3.X<P1, P2, P3, R>
    of(Func3<P1, P2, P3, R> func) {
        return Func3.X.of(func);
    }

    public static <P1, P2, P3, P4, R> Func4.X<P1, P2, P3, P4, R>
    of(Func4<P1, P2, P3, P4, R> func) {
        return Func4.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, R> Func5.X<P1, P2, P3, P4, P5, R>
    of(Func5<P1, P2, P3, P4, P5, R> func) {
        return Func5.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, R> Func6.X<P1, P2, P3, P4, P5, P6, R>
    of(Func6<P1, P2, P3, P4, P5, P6, R> func) {
        return Func6.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R>
    Func7.X<P1, P2, P3, P4, P5, P6, P7, R>
    of(Func7<P1, P2, P3, P4, P5, P6, P7, R> func) {
        return Func7.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
    Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R>
    of(Func8<P1, P2, P3, P4, P5, P6, P7, P8, R> func) {
        return Func8.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    of(Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
        return Func9.X.of(func);
    }

    private final R result;

    private Functions(R result) {
        this.result = result;
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
    public R invoke() {
        return result;
    }

    @Override
    public R invoke(P1 p1) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3, P4 p4) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        return result;
    }

    @Override
    public R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
        return result;
    }

    @Override
    public R invoke(Object... objects) {
        return result;
    }
}
