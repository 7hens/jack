package cn.thens.jack.flow;


import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
public interface Emitter<T> extends Collector<T>, Cancellable, IScheduler, BackPressure<T> {
    @Override
    void post(Reply<? extends T> reply);

    void next(T data);

    void error(Throwable error);

    void complete();
}
