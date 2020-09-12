package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
public interface IScheduler {
    Cancellable schedule(Runnable runnable);
}
