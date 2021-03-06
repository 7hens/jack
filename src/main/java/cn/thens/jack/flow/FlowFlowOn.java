package cn.thens.jack.flow;


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
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        Emitter<T> upEmitter = createEmitter(upScheduler, emitter);
        emitter.addCancellable(upEmitter);
        upEmitter.addCancellable(upScheduler.schedule(() -> {
            try {
                upFlow.onStartCollect(upEmitter);
            } catch (Throwable e) {
                emitter.error(e);
            }
        }));
    }
}
