package cn.thens.jack;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class JOnce {
    public static <R> Func<R> func() {
        return new Func<>();
    }

    public static Action action() {
        return new Action();
    }

    public static final class Func<R> {
        private AtomicBoolean shouldEnter = new AtomicBoolean(true);
        private AtomicBoolean shouldLeave = new AtomicBoolean(false);
        private R result;

        private Func() {
        }

        public R call(JFunc.F0<? extends R> func) {
            if (result != null) return result;
            if (shouldEnter.compareAndSet(true, false)) {
                try {
                    result = func.call();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    shouldLeave.set(true);
                }
            }
            //noinspection StatementWithEmptyBody
            while (!shouldLeave.get()) ;
            return result;
        }

    }

    public static final class Action {
        private Func<Object> once = func();
        private static Object ONCE_RESULT = new Object();

        private Action() {
        }

        public void run(JFunc.A0 func) {
            once.call(() -> {
                func.run();
                return ONCE_RESULT;
            });
        }
    }

    public static <R>
    JFunc.F0<R>
    wrap(final JFunc.F0<R> func) {
        final Func<R> once = func();
        return () ->
                once.call(func);
    }

    public static <P1, R>
    JFunc.F1<P1, R>
    wrap(final JFunc.F1<P1, R> func) {
        final Func<R> once = func();
        return p1 ->
                once.call(() -> func.call(p1));
    }

    public static <P1, P2, R>
    JFunc.F2<P1, P2, R>
    wrap(final JFunc.F2<P1, P2, R> func) {
        final Func<R> once = func();
        return (p1, p2) ->
                once.call(() -> func.call(p1, p2));
    }

    public static <P1, P2, P3, R>
    JFunc.F3<P1, P2, P3, R>
    wrap(final JFunc.F3<P1, P2, P3, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3) ->
                once.call(() -> func.call(p1, p2, p3));
    }

    public static <P1, P2, P3, P4, R>
    JFunc.F4<P1, P2, P3, P4, R>
    wrap(final JFunc.F4<P1, P2, P3, P4, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4) ->
                once.call(() -> func.call(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5, R>
    JFunc.F5<P1, P2, P3, P4, P5, R>
    wrap(final JFunc.F5<P1, P2, P3, P4, P5, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6, R>
    JFunc.F6<P1, P2, P3, P4, P5, P6, R>
    wrap(final JFunc.F6<P1, P2, P3, P4, P5, P6, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R>
    JFunc.F7<P1, P2, P3, P4, P5, P6, P7, R>
    wrap(final JFunc.F7<P1, P2, P3, P4, P5, P6, P7, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
    JFunc.F8<P1, P2, P3, P4, P5, P6, P7, P8, R>
    wrap(final JFunc.F8<P1, P2, P3, P4, P5, P6, P7, P8, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    JFunc.F9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    wrap(final JFunc.F9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static <R>
    JFunc.FN<R>
    wrap(final JFunc.FN<R> func) {
        final Func<R> once = func();
        return params ->
                once.call(() -> func.call(params));
    }

    public static JFunc.A0
    wrap(final JFunc.A0 func) {
        final Action once = action();
        return () ->
                once.run(func);
    }

    public static <P1>
    JFunc.A1<P1>
    wrap(final JFunc.A1<P1> func) {
        final Action once = action();
        return p1 ->
                once.run(() -> func.run(p1));
    }

    public static <P1, P2>
    JFunc.A2<P1, P2>
    wrap(final JFunc.A2<P1, P2> func) {
        final Action once = action();
        return (p1, p2) ->
                once.run(() -> func.run(p1, p2));
    }

    public static <P1, P2, P3>
    JFunc.A3<P1, P2, P3>
    wrap(final JFunc.A3<P1, P2, P3> func) {
        final Action once = action();
        return (p1, p2, p3) ->
                once.run(() -> func.run(p1, p2, p3));
    }

    public static <P1, P2, P3, P4>
    JFunc.A4<P1, P2, P3, P4>
    wrap(final JFunc.A4<P1, P2, P3, P4> func) {
        final Action once = action();
        return (p1, p2, p3, p4) ->
                once.run(() -> func.run(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5>
    JFunc.A5<P1, P2, P3, P4, P5>
    wrap(final JFunc.A5<P1, P2, P3, P4, P5> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5) ->
                once.run(() -> func.run(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6>
    JFunc.A6<P1, P2, P3, P4, P5, P6>
    wrap(final JFunc.A6<P1, P2, P3, P4, P5, P6> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6) ->
                once.run(() -> func.run(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7>
    JFunc.A7<P1, P2, P3, P4, P5, P6, P7>
    wrap(final JFunc.A7<P1, P2, P3, P4, P5, P6, P7> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7) ->
                once.run(() -> func.run(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8>
    JFunc.A8<P1, P2, P3, P4, P5, P6, P7, P8>
    wrap(final JFunc.A8<P1, P2, P3, P4, P5, P6, P7, P8> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8) ->
                once.run(() -> func.run(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    JFunc.A9<P1, P2, P3, P4, P5, P6, P7, P8, P9>
    wrap(final JFunc.A9<P1, P2, P3, P4, P5, P6, P7, P8, P9> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                once.run(() -> func.run(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static JFunc.AN
    wrap(final JFunc.AN func) {
        final Action once = action();
        return params ->
                once.run(() -> func.run(params));
    }
}
