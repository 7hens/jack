package cn.thens.jack.scheduler;

import android.os.Looper;

import cn.thens.jack.ref.Ref;

/**
 * @author 7hens
 */
@SuppressWarnings("WeakerAccess")
public final class AndroidSchedulers {
    public static Scheduler from(Looper looper) {
        return new LooperScheduler(looper);
    }

    private static final Ref<Scheduler> MAIN_THREAD = Ref.lazy(() -> from(Looper.getMainLooper()));

    public static Scheduler mainThread() {
        return MAIN_THREAD.get();
    }

    public static Scheduler myThread() {
        return from(Looper.myLooper());
    }
}
