package cn.thens.jack.flow;

import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
class FlowTransform<T, R> extends Flow<R> {
    private final Flow<T> upFlow;
    private final FlowOperator<? super T, ? extends R> operator;

    FlowTransform(Flow<T> upFlow, FlowOperator<? super T, ? extends R> operator) {
        this.upFlow = upFlow;
        this.operator = operator;
    }

    @Override
    protected Cancellable collect(IScheduler scheduler, Collector<? super R> collector) {
        CollectorEmitter<? super R> emitter = CollectorEmitter.create(scheduler, collector);
        try {
            upFlow.collect(emitter, operator.apply(emitter));
        } catch (Throwable e) {
            collector.post(Reply.error(e));
        }
        return emitter;
    }
}
