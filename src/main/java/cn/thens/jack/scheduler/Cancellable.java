package cn.thens.jack.scheduler;

/**
 * @author 7hens
 */
public interface Cancellable extends ICancellable {
    boolean isCancelled();

    void addCancellable(ICancellable onCancel);

    void into(Cancellable cancellable);
}
