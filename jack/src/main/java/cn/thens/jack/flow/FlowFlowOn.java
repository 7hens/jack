package cn.thens.jack.flow;


import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
class FlowFlowOn<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final IScheduler upScheduler;

    FlowFlowOn(Flow<T> upFlow, IScheduler upScheduler) {
        this.upFlow = upFlow;
        this.upScheduler = upScheduler;
    }

    @Override
    protected Cancellable collect(IScheduler scheduler, Collector<? super T> collector) {
        if (scheduler == upScheduler) {
            return upFlow.collect(scheduler, collector);
        }
        return upFlow.collect(upScheduler, CollectorEmitter.create(scheduler, collector));
    }
}
