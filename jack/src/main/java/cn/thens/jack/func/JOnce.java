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

        public R call(JFunc0<? extends R> func) {
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

        public void call(JAction0 func) {
            once.call(() -> {
                func.invoke();
                return ONCE_RESULT;
            });
        }
    }

    public static <R>
    JFunc0<R>
    wrap(final JFunc0<R> func) {
        final Func<R> once = func();
        return () ->
                once.call(func);
    }

    public static <P1, R>
    JFunc1<P1, R>
    wrap(final JFunc1<P1, R> func) {
        final Func<R> once = func();
        return p1 ->
                once.call(() -> func.invoke(p1));
    }

    public static <P1, P2, R>
    JFunc2<P1, P2, R>
    wrap(final JFunc2<P1, P2, R> func) {
        final Func<R> once = func();
        return (p1, p2) ->
                once.call(() -> func.invoke(p1, p2));
    }

    public static <P1, P2, P3, R>
    JFunc3<P1, P2, P3, R>
    wrap(final JFunc3<P1, P2, P3, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3) ->
                once.call(() -> func.invoke(p1, p2, p3));
    }

    public static <P1, P2, P3, P4, R>
    JFunc4<P1, P2, P3, P4, R>
    wrap(final JFunc4<P1, P2, P3, P4, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4) ->
                once.call(() -> func.invoke(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5, R>
    JFunc5<P1, P2, P3, P4, P5, R>
    wrap(final JFunc5<P1, P2, P3, P4, P5, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6, R>
    JFunc6<P1, P2, P3, P4, P5, P6, R>
    wrap(final JFunc6<P1, P2, P3, P4, P5, P6, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R>
    JFunc7<P1, P2, P3, P4, P5, P6, P7, R>
    wrap(final JFunc7<P1, P2, P3, P4, P5, P6, P7, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R>
    JFunc8<P1, P2, P3, P4, P5, P6, P7, P8, R>
    wrap(final JFunc8<P1, P2, P3, P4, P5, P6, P7, P8, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    JFunc9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>
    wrap(final JFunc9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> func) {
        final Func<R> once = func();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static <R>
    JFuncN<R>
    wrap(final JFuncN<R> func) {
        final Func<R> once = func();
        return params ->
                once.call(() -> func.invoke(params));
    }

    public static JAction0
    wrap(final JAction0 func) {
        final Action once = action();
        return () ->
                once.call(func);
    }

    public static <P1>
    JAction1<P1>
    wrap(final JAction1<P1> func) {
        final Action once = action();
        return p1 ->
                once.call(() -> func.invoke(p1));
    }

    public static <P1, P2>
    JAction2<P1, P2>
    wrap(final JAction2<P1, P2> func) {
        final Action once = action();
        return (p1, p2) ->
                once.call(() -> func.invoke(p1, p2));
    }

    public static <P1, P2, P3>
    JAction3<P1, P2, P3>
    wrap(final JAction3<P1, P2, P3> func) {
        final Action once = action();
        return (p1, p2, p3) ->
                once.call(() -> func.invoke(p1, p2, p3));
    }

    public static <P1, P2, P3, P4>
    JAction4<P1, P2, P3, P4>
    wrap(final JAction4<P1, P2, P3, P4> func) {
        final Action once = action();
        return (p1, p2, p3, p4) ->
                once.call(() -> func.invoke(p1, p2, p3, p4));
    }

    public static <P1, P2, P3, P4, P5>
    JAction5<P1, P2, P3, P4, P5>
    wrap(final JAction5<P1, P2, P3, P4, P5> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5));
    }

    public static <P1, P2, P3, P4, P5, P6>
    JAction6<P1, P2, P3, P4, P5, P6>
    wrap(final JAction6<P1, P2, P3, P4, P5, P6> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6));
    }

    public static <P1, P2, P3, P4, P5, P6, P7>
    JAction7<P1, P2, P3, P4, P5, P6, P7>
    wrap(final JAction7<P1, P2, P3, P4, P5, P6, P7> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8>
    JAction8<P1, P2, P3, P4, P5, P6, P7, P8>
    wrap(final JAction8<P1, P2, P3, P4, P5, P6, P7, P8> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8));
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9>
    JAction9<P1, P2, P3, P4, P5, P6, P7, P8, P9>
    wrap(final JAction9<P1, P2, P3, P4, P5, P6, P7, P8, P9> func) {
        final Action once = action();
        return (p1, p2, p3, p4, p5, p6, p7, p8, p9) ->
                once.call(() -> func.invoke(p1, p2, p3, p4, p5, p6, p7, p8, p9));
    }

    public static JActionN
    wrap(final JActionN func) {
        final Action once = action();
        return params ->
                once.call(() -> func.invoke(params));
    }
}
