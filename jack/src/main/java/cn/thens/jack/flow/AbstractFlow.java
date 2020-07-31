package cn.thens.jack.flow;


import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Scheduler;

/**
 * @author 7hens
 */
abstract class AbstractFlow<T> extends Flow<T> {
    @Override
    protected Cancellable collect(Scheduler scheduler, Collector<? super T> collector) {
        CollectorEmitter<? super T> emitter = CollectorEmitter.create(scheduler, collector);
        emitter.scheduler().schedule(new Runnable() {
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

    protected abstract void onStart(Emitter<? super T> emitter) throws Throwable;
}
