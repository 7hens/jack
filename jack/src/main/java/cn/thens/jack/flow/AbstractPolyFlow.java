package cn.thens.jack.flow;


import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
abstract class AbstractPolyFlow<T> extends PolyFlow<T> {
    @Override
    protected Cancellable collect(IScheduler scheduler, Collector<? super IFlow<T>> collector) {
        CollectorEmitter<? super IFlow<T>> emitter = CollectorEmitter.create(scheduler, collector);
        emitter.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    onStart(emitter);
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        });
        return emitter;
    }

    protected abstract void onStart(Emitter<? super IFlow<T>> emitter) throws Throwable;
}
