package cn.thens.jack.flow;


import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.Scheduler;

/**
 * @author 7hens
 */
class FlowFlowOn<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final Scheduler upScheduler;

    FlowFlowOn(Flow<T> upFlow, Scheduler upScheduler) {
        this.upFlow = upFlow;
        this.upScheduler = upScheduler;
    }

    @Override
    protected Cancellable collect(Scheduler scheduler, Collector<? super T> collector) {
        return upFlow.collect(upScheduler, CollectorEmitter.create(scheduler, collector));
    }
}
