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

        public R invoke(JFunc.P0<? extends R> func) {
            if (result != null) return result;
            if (shouldEnter.compareAndSet(true, false)) {
                try {
                    result = func.invoke();
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

        public void invoke(JAction.P0 func) {
            once.invoke(() -> {
                func.invoke();
                return ONCE_RESULT;
            });
        }

    }

    public static <R>
    JFunc.P0<R> wrap(final JFunc.P0<R> func) {
        final Func<R> once = func();
        return () -> once.invoke(func);
    }

    public static <P1, R>
    JFunc.P1<P1, R> wrap(final JFunc.P1<P1, R> func) {
        final Func<R> once = func();
        return p1 -> once.invoke(() -> func.invoke(p1));
    }

    public static <P1, P2, R>
    JFunc.P2<P1, P2, R> wrap(final JFunc.P2<P1, P2, R> func) {
        final Func<R> once = func();
        return (p1, p2) -> once.invoke(() -> func.invoke(p1, p2));
    }

    public static <P1, P2, P3, R>
    JFunc.P3<P1, P2, P3, R> wrap(final JFunc.P3<P1, P2, P3, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3) -> once.invoke(() -> func.invoke(p1, p2, p3));
    }

    public static <P1, P2, P3, P4, R>
    JFunc.P4<P1, P2, P3, P4, R> wrap(final JFunc.P4<P1, P2, P3, P4, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4) -> once.invoke(() -> func.invoke(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5, R>
    JFunc.P5<P1, P2, P3, P4, P5, R> wrap(final JFunc.P5<P1, P2, P3, P4, P5, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6, R>
    JFunc.P6<P1, P2, P3, P4, P5, P6, R> wrap(final JFunc.P6<P1, P2, P3, P4, P5, P6, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R>
    JFunc.P7<P1, P2, P3, P4, P5, P6, P7, R> wrap(final JFunc.P7<P1, P2, P3, P4, P5, P6, P7, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
    JFunc.P8<P1, P2, P3, P4, P5, P6, P7, P8, R> wrap(final JFunc.P8<P1, P2, P3, P4, P5, P6, P7, P8, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    JFunc.P9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> wrap(final JFunc.P9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static <R>
    JFunc.Pn<R> wrap(final JFunc.Pn<R> func) {
        final Func<R> once = func();
        return params -> once.invoke(() -> func.invoke(params));
    }

    public static JAction.P0 wrap(final JAction.P0 func) {
        final Action once = action();
        return () -> once.invoke(func);
    }

    public static <P1>
    JAction.P1<P1> wrap(final JAction.P1<P1> func) {
        final Action once = action();
        return p1 -> once.invoke(() -> func.invoke(p1));
    }

    public static <P1, P2>
    JAction.P2<P1, P2> wrap(final JAction.P2<P1, P2> func) {
        final Action once = action();
        return (p1, p2) -> once.invoke(() -> func.invoke(p1, p2));
    }

    public static <P1, P2, P3>
    JAction.P3<P1, P2, P3> wrap(final JAction.P3<P1, P2, P3> func) {
        final Action once = action();
        return (p1, p2, p3) -> once.invoke(() -> func.invoke(p1, p2, p3));
    }

    public static <P1, P2, P3, P4>
    JAction.P4<P1, P2, P3, P4> wrap(final JAction.P4<P1, P2, P3, P4> func) {
        final Action once = action();
        return (p1, p2, p3, p4) -> once.invoke(() -> func.invoke(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5>
    JAction.P5<P1, P2, P3, P4, P5> wrap(final JAction.P5<P1, P2, P3, P4, P5> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6>
    JAction.P6<P1, P2, P3, P4, P5, P6> wrap(final JAction.P6<P1, P2, P3, P4, P5, P6> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7>
    JAction.P7<P1, P2, P3, P4, P5, P6, P7> wrap(final JAction.P7<P1, P2, P3, P4, P5, P6, P7> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8>
    JAction.P8<P1, P2, P3, P4, P5, P6, P7, P8> wrap(final JAction.P8<P1, P2, P3, P4, P5, P6, P7, P8> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    JAction.P9<P1, P2, P3, P4, P5, P6, P7, P8, P9> wrap(final JAction.P9<P1, P2, P3, P4, P5, P6, P7, P8, P9> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) -> once.invoke(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static JAction.Pn wrap(final JAction.Pn func) {
        final Action once = action();
        return params -> once.invoke(() -> func.invoke(params));
    }
}
