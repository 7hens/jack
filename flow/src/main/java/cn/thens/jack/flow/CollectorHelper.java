package cn.thens.jack.flow;


import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
public abstract class CollectorHelper<T> implements Collector<T> {
    @Override
    public void post(Reply<? extends T> reply) throws Throwable {
        if (!reply.isTerminal()) {
            onEach(reply.data());
            return;
        }
        Throwable error = reply.error();
        onTerminate(error);
        if (reply.isComplete()) {
            onComplete();
            return;
        }
        if (reply.isCancel()) {
            onCancel();
            return;
        }
        onError(error);
    }

    protected void onStart(Cancellable cancellable) throws Throwable {
    }

    protected void onEach(T data) throws Throwable {
    }

    protected void onTerminate(Throwable error) throws Throwable {
    }

    protected void onComplete() throws Throwable {
    }

    protected void onError(Throwable error) throws Throwable {
    }

    protected void onCancel() throws Throwable {
    }

    @SuppressWarnings("rawtypes")
    private static CollectorHelper INSTANCE = new CollectorHelper() {
    };

    @SuppressWarnings("unchecked")
    public static <T> CollectorHelper<T> empty() {
        return INSTANCE;
    }
}
