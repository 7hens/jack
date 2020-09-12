package cn.thens.jack.loq;

@PrettyLogger.Ignored
public final class Loq {
    private Loq() {
    }

    public static LoqX tag(String tag) {
        return LoqX.getDefaultInstance().tag(tag);
    }

    public static LoqX onlyIf(boolean sure) {
        return LoqX.getDefaultInstance().onlyIf(sure);
    }

    public static LoqX logger(Logger<? super Object> logger) {
        return LoqX.getDefaultInstance().logger(logger);
    }

    public static LoqX v(Object msg) {
        return LoqX.getDefaultInstance().v(msg);
    }

    public static LoqX d(Object msg) {
        return LoqX.getDefaultInstance().d(msg);
    }

    public static LoqX i(Object msg) {
        return LoqX.getDefaultInstance().i(msg);
    }

    public static LoqX w(Object msg) {
        return LoqX.getDefaultInstance().w(msg);
    }

    public static LoqX e(Object msg) {
        return LoqX.getDefaultInstance().e(msg);
    }

    public static LoqX wtf(Object msg) {
        return LoqX.getDefaultInstance().wtf(msg);
    }

    public static LoqX log(int priority, Object msg) {
        return LoqX.getDefaultInstance().log(priority, msg);
    }

}
