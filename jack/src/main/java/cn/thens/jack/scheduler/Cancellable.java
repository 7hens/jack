package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
public interface Cancellable {
    void cancel();

    boolean isCancelled();
}
