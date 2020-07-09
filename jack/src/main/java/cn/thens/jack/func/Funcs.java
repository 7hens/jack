package cn.thens.jack.func;

@SuppressWarnings("unchecked")
public class Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> implements
        Func0<R>,
        Func1<P1, R>,
        Func2<P1, P2, R>,
        Func3<P1, P2, P3, R>,
        Func4<P1, P2, P3, P4, R>,
        Func5<P1, P2, P3, P4, P5, R>,
        Func6<P1, P2, P3, P4, P5, P6, R>,
        Func7<P1, P2, P3, P4, P5, P6, P7, R>,
        Func8<P1, P2, P3, P4, P5, P6, P7, P8, R>,
        Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> {

    private static Funcs EMPTY = new Funcs();

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> empty() {
        return EMPTY;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> always(R result) {
        if (result == null) return empty();
        return from((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> result);
    }

    private static Funcs SELF = from((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> p1);

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, P1> self() {
        return SELF;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> from(Func9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
        Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> funcX = of(func);
        return new Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>() {
            @Override
            public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
                return funcX.call(p1, p2, p3, p4, p5, p6, p7, p8, p9);
            }
        };
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Funcs<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> error(Throwable error) {
        return from((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> {
            throw error;
        });
    }

    public static <R> Func0.X<R>
    of(Func0<? extends R> func) {
        return Func0.X.of(func);
    }

    public static <P1, R> Func1.X<P1, R>
    of(Func1<? super P1, ? extends R> func) {
        return Func1.X.of(func);
    }

    public static <P1, P2, R> Func2.X<P1, P2, R>
    of(Func2<? super P1, ? super P2, ? extends R> func) {
        return Func2.X.of(func);
    }

    public static <P1, P2, P3, R> Func3.X<P1, P2, P3, R>
    of(Func3<? super P1, ? super P2, ? super P3, ? extends R> func) {
        return Func3.X.of(func);
    }

    public static <P1, P2, P3, P4, R> Func4.X<P1, P2, P3, P4, R>
    of(Func4<? super P1, ? super P2, ? super P3, ? super P4, ? extends R> func) {
        return Func4.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, R> Func5.X<P1, P2, P3, P4, P5, R>
    of(Func5<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? extends R> func) {
        return Func5.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, R> Func6.X<P1, P2, P3, P4, P5, P6, R>
    of(Func6<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? extends R> func) {
        return Func6.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R>
    Func7.X<P1, P2, P3, P4, P5, P6, P7, R>
    of(Func7<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? extends R> func) {
        return Func7.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
    Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R>
    of(Func8<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8, ? extends R> func) {
        return Func8.X.of(func);
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    of(Func9<? super P1, ? super P2, ? super P3, ? super P4, ? super P5, ? super P6, ? super P7, ? super P8, ? super P9, ? extends R> func) {
        return Func9.X.of(func);
    }

    public Func1.X<P1, R> x1() {
        return Func1.X.of(this);
    }

    public Func2.X<P1, P2, R> x2() {
        return Func2.X.of(this);
    }

    public Func3.X<P1, P2, P3, R> x3() {
        return Func3.X.of(this);
    }

    public Func4.X<P1, P2, P3, P4, R> x4() {
        return Func4.X.of(this);
    }

    public Func5.X<P1, P2, P3, P4, P5, R> x5() {
        return Func5.X.of(this);
    }

    public Func6.X<P1, P2, P3, P4, P5, P6, R> x6() {
        return Func6.X.of(this);
    }

    public Func7.X<P1, P2, P3, P4, P5, P6, P7, R> x7() {
        return Func7.X.of(this);
    }

    public Func8.X<P1, P2, P3, P4, P5, P6, P7, P8, R> x8() {
        return Func8.X.of(this);
    }

    public Func9.X<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> x9() {
        return Func9.X.of(this);
    }

    @Override
    public R call() {
        return call(null);
    }

    @Override
    public R call(P1 p1) {
        return call(p1, null);
    }

    @Override
    public R call(P1 p1, P2 p2) {
        return call(p1, p2, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3) {
        return call(p1, p2, p3, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3, P4 p4) {
        return call(p1, p2, p3, p4, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        return call(p1, p2, p3, p4, p5, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        return call(p1, p2, p3, p4, p5, p6, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        return call(p1, p2, p3, p4, p5, p6, p7, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        return call(p1, p2, p3, p4, p5, p6, p7, p8, null);
    }

    @Override
    public R call(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) {
        return null;
    }
}
