package cn.thens.jack.scheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

/**
 * @author 7hens
 */
class LooperScheduler extends Scheduler {
    private final Handler handler;

    LooperScheduler(Looper looper) {
        this.handler = new Handler(looper);
    }

    @Override
    public Cancellable schedule(Runnable runnable) {
        handler.post(runnable);
        return cancellableOf(runnable);
    }

    @Override
    public Cancellable schedule(final Runnable runnable, long delay, TimeUnit unit) {
        handler.postDelayed(runnable, unit.toMillis(delay));
        return cancellableOf(runnable);
    }

    private Cancellable cancellableOf(Runnable runnable) {
        return new CompositeCancellable() {
            @Override
            protected void onCancel() {
                super.onCancel();
                handler.removeCallbacks(runnable);
            }
        };
    }
}
