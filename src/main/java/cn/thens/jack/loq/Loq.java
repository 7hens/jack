package cn.thens.jack.loq;

@PrettyLogger.Ignored
public final class Loq {
    private Loq() {
    }

    public static LoqX tag(String tag) {
        return get().tag(tag);
    }

    public static LoqX onlyIf(boolean sure) {
        return get().onlyIf(sure);
    }

    public static LoqX logger(Logger<? super Object> logger) {
        return get().logger(logger);
    }

    public static LoqX v(Object msg) {
        return get().v(msg);
    }

    public static LoqX d(Object msg) {
        return get().d(msg);
    }

    public static LoqX i(Object msg) {
        return get().i(msg);
    }

    public static LoqX w(Object msg) {
        return get().w(msg);
    }

    public static LoqX e(Object msg) {
        return get().e(msg);
    }

    public static LoqX wtf(Object msg) {
        return get().wtf(msg);
    }

    public static LoqX log(int priority, Object msg) {
        return get().log(priority, msg);
    }

    private static LoqX get() {
        return LoqX.getDefaultInstance();
    }
}
