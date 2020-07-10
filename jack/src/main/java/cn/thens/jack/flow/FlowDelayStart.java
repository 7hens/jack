package cn.thens.jack.flow;


/**
 * @author 7hens
 */
class FlowDelayStart<T> extends AbstractFlow<T> {
    private final Flow<T> upFlow;
    private final IFlow<?> delayFlow;

    private FlowDelayStart(Flow<T> upFlow, IFlow<?> delayFlow) {
        this.upFlow = upFlow;
        this.delayFlow = delayFlow;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onStart(CollectorEmitter<? super T> emitter) {
        try {
            delayFlow.asFlow().collect(emitter, new CollectorHelper() {
                @Override
                protected void onTerminate(Throwable error) throws Throwable {
                    super.onTerminate(error);
                    upFlow.collect(emitter);
                }
            });
        } catch (Throwable e) {
            emitter.error(e);
        }
    }

    public static <T> FlowDelayStart<T> delayStart(Flow<T> upFlow, IFlow<?> delayFlow) {
        return new FlowDelayStart<>(upFlow, delayFlow);
    }
}
