package cn.thens.jack.func;

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

        public R call(JFunc.T0<? extends R> func) {
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

        public void call(JAction.T0 func) {
            once.call(() -> {
                func.call();
                return ONCE_RESULT;
            });
        }
    }

    public static <R>
    JFunc.T0<R>
    wrap(final JFunc.T0<R> func) {
        final Func<R> once = func();
        return () ->
                once.call(func);
    }

    public static <P1, R>
    JFunc.T1<P1, R>
    wrap(final JFunc.T1<P1, R> func) {
        final Func<R> once = func();
        return p1 ->
                once.call(() -> func.call(p1));
    }

    public static <P1, P2, R>
    JFunc.T2<P1, P2, R>
    wrap(final JFunc.T2<P1, P2, R> func) {
        final Func<R> once = func();
        return (p1, p2) ->
                once.call(() -> func.call(p1, p2));
    }

    public static <P1, P2, P3, R>
    JFunc.T3<P1, P2, P3, R>
    wrap(final JFunc.T3<P1, P2, P3, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3) ->
                once.call(() -> func.call(p1, p2, p3));
    }

    public static <P1, P2, P3, P4, R>
    JFunc.T4<P1, P2, P3, P4, R>
    wrap(final JFunc.T4<P1, P2, P3, P4, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4) ->
                once.call(() -> func.call(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5, R>
    JFunc.T5<P1, P2, P3, P4, P5, R>
    wrap(final JFunc.T5<P1, P2, P3, P4, P5, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6, R>
    JFunc.T6<P1, P2, P3, P4, P5, P6, R>
    wrap(final JFunc.T6<P1, P2, P3, P4, P5, P6, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R>
    JFunc.T7<P1, P2, P3, P4, P5, P6, P7, R>
    wrap(final JFunc.T7<P1, P2, P3, P4, P5, P6, P7, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
    JFunc.T8<P1, P2, P3, P4, P5, P6, P7, P8, R>
    wrap(final JFunc.T8<P1, P2, P3, P4, P5, P6, P7, P8, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    JFunc.T9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    wrap(final JFunc.T9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static <R>
    JFunc.TN<R>
    wrap(final JFunc.TN<R> func) {
        final Func<R> once = func();
        return params ->
                once.call(() -> func.call(params));
    }

    public static JAction.T0
    wrap(final JAction.T0 func) {
        final Action once = action();
        return () ->
                once.call(func);
    }

    public static <P1>
    JAction.T1<P1>
    wrap(final JAction.T1<P1> func) {
        final Action once = action();
        return p1 ->
                once.call(() -> func.call(p1));
    }

    public static <P1, P2>
    JAction.T2<P1, P2>
    wrap(final JAction.T2<P1, P2> func) {
        final Action once = action();
        return (p1, p2) ->
                once.call(() -> func.call(p1, p2));
    }

    public static <P1, P2, P3>
    JAction.T3<P1, P2, P3>
    wrap(final JAction.T3<P1, P2, P3> func) {
        final Action once = action();
        return (p1, p2, p3) ->
                once.call(() -> func.call(p1, p2, p3));
    }

    public static <P1, P2, P3, P4>
    JAction.T4<P1, P2, P3, P4>
    wrap(final JAction.T4<P1, P2, P3, P4> func) {
        final Action once = action();
        return (p1, p2, p3, p4) ->
                once.call(() -> func.call(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5>
    JAction.T5<P1, P2, P3, P4, P5>
    wrap(final JAction.T5<P1, P2, P3, P4, P5> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6>
    JAction.T6<P1, P2, P3, P4, P5, P6>
    wrap(final JAction.T6<P1, P2, P3, P4, P5, P6> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7>
    JAction.T7<P1, P2, P3, P4, P5, P6, P7>
    wrap(final JAction.T7<P1, P2, P3, P4, P5, P6, P7> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8>
    JAction.T8<P1, P2, P3, P4, P5, P6, P7, P8>
    wrap(final JAction.T8<P1, P2, P3, P4, P5, P6, P7, P8> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    JAction.T9<P1, P2, P3, P4, P5, P6, P7, P8, P9>
    wrap(final JAction.T9<P1, P2, P3, P4, P5, P6, P7, P8, P9> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                once.call(() -> func.call(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static JAction.TN
    wrap(final JAction.TN func) {
        final Action once = action();
        return params ->
                once.call(() -> func.call(params));
    }
}
