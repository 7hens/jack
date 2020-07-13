package cn.thens.jack.scheduler;


/**
 * @author 7hens
 */
public abstract class CancellableScheduler extends Scheduler implements Cancellable {
    public abstract CancellableScheduler flat();
}
