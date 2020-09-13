package cn.thens.jack.loq;

import android.os.SystemClock;
import android.util.Log;
import android.util.LruCache;

import org.jetbrains.annotations.NotNull;

@PrettyLogger.Ignored
public final class LoqX {
    private static final String DEFAULT_TAG = "$";

    private final String tag;
    private final Logger<? super Object> logger;

    private LoqX(String tag, Logger<? super Object> logger) {
        this.tag = tag;
        this.logger = logger;
    }

    public LoqX tag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return this;
        }
        return new LoqX(this.tag + "/" + tag, logger);
    }

    public LoqX logger(Logger<? super Object> logger) {
        return new LoqX(tag, logger);
    }

    public LoqX onlyIf(final boolean sure) {
        return sure ? this : EMPTY;
    }

    public LoqX v(Object msg) {
        return log(Log.VERBOSE, msg);
    }

    public LoqX d(Object msg) {
        return log(Log.DEBUG, msg);
    }

    public LoqX i(Object msg) {
        return log(Log.INFO, msg);
    }

    public LoqX w(Object msg) {
        return log(Log.WARN, msg);
    }

    public LoqX e(Object msg) {
        return log(Log.ERROR, msg);
    }

    public LoqX wtf(Object msg) {
        return log(Log.ASSERT, msg);
    }

    public LoqX log(int priority, Object msg) {
        logger.log(priority, tag, msg);
        return this;
    }

    private static final LoqX EMPTY = new LoqX(DEFAULT_TAG, Logger.EMPTY);
    private static LoqX DEFAULT = new LoqX(DEFAULT_TAG, new PrettyLogger(Logger.LOGCAT));

    public static LoqX getDefaultInstance() {
        return DEFAULT;
    }

    public static void setDefaultInstance(LoqX value) {
        DEFAULT = value;
    }

    private static final int LRU_CACHE_MAX_SIZE = 256;
    private static LruCache<String, Long> timers = new LruCache<>(LRU_CACHE_MAX_SIZE);
    private static LruCache<String, Long> counters = new LruCache<>(LRU_CACHE_MAX_SIZE);

    public static Object time(final String name) {
        return new Object() {
            @NotNull
            @Override
            public String toString() {
                long now = SystemClock.elapsedRealtime();
                Long lastTime = timers.get(name);
                if (lastTime == null) lastTime = 0L;
                timers.put(name, now);
                return "time(" + name + "): " + (now - lastTime) + "ms";
            }
        };
    }

    public static Object count(final String name) {
        return new Object() {
            @NotNull
            @Override
            public String toString() {
                Long lastCount = counters.get(name);
                if (lastCount == null) lastCount = 0L;
                counters.put(name, lastCount + 1);
                return "count(" + name + "): " + (lastCount + 1);
            }
        };
    }
}
