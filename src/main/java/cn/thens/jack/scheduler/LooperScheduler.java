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
        OneOffJob job = new OneOffJob(runnable);
        handler.post(job);
        job.addCancellable(() -> handler.removeCallbacks(job));
        return job;
    }

    @Override
    public Cancellable schedule(final Runnable runnable, long delay, TimeUnit unit) {
        OneOffJob job = new OneOffJob(runnable);
        handler.postDelayed(job, unit.toMillis(delay));
        job.addCancellable(() -> handler.removeCallbacks(job));
        return job;
    }
}
