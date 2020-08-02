package cn.thens.jack.flow;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.func.Func1;
import cn.thens.jack.func.Funcs;
import cn.thens.jack.func.Predicate;
import cn.thens.jack.scheduler.Cancellable;

/**
 * @author 7hens
 */
abstract class FlowFilter<T> extends Flow<T> {
    private final Flow<T> upFlow;

    private FlowFilter(Flow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, new Collector<T>() {
            @Override
            public void post(Reply<? extends T> reply) {
                try {
                    if (reply.isTerminal()) {
                        onTerminated(emitter, reply.error());
                        return;
                    }
                    T data = reply.data();
                    if (test(data)) {
                        emitter.data(data);
                    }
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        });
    }

    abstract boolean test(T data) throws Throwable;

    void onTerminated(Emitter<? super T> emitter, Throwable error) throws Throwable {
        emitter.error(error);
    }

    static <T> FlowFilter<T> filter(Flow<T> upFlow, Predicate<? super T> predicate) {
        return new FlowFilter<T>(upFlow) {

            @Override
            boolean test(T data) throws Throwable {
                return predicate.test(data);
            }
        };
    }

    static <T, K> Flow<T> distinctBy(final Flow<T> upFlow, final Func1<? super T, ? extends K> keySelector) {
        return defer(() -> new FlowFilter<T>(upFlow) {
            private Set<K> collectedKeys = new HashSet<>();

            @Override
            public boolean test(T data) throws Throwable {
                K key = keySelector.call(data);
                if (collectedKeys.contains(key)) {
                    return false;
                }
                collectedKeys.add(key);
                return true;
            }

            @Override
            protected void onTerminated(Emitter<? super T> emitter, Throwable error) throws Throwable {
                super.onTerminated(emitter, error);
                collectedKeys.clear();
            }
        });
    }

    static <T> Flow<T> distinct(Flow<T> upFlow) {
        return distinctBy(upFlow, Funcs.self());
    }

    static <T, K> Flow<T> distinctUntilChangedBy(Flow<T> upFlow, final Func1<? super T, ? extends K> keySelector) {
        return defer(() -> new FlowFilter<T>(upFlow) {
            private K lastKey = null;

            @Override
            public boolean test(T data) throws Throwable {
                K key = keySelector.call(data);
                if (key.equals(lastKey)) {
                    return false;
                }
                lastKey = key;
                return true;
            }

            @Override
            protected void onTerminated(Emitter<? super T> emitter, Throwable error) throws Throwable {
                super.onTerminated(emitter, error);
                lastKey = null;
            }
        });
    }

    static <T> Flow<T> distinctUntilChanged(Flow<T> upFlow) {
        return distinctUntilChangedBy(upFlow, Funcs.self());
    }

    static <T> Flow<T> last(Flow<T> upFlow, Predicate<? super T> predicate) {
        return defer(() -> new FlowFilter<T>(upFlow) {
            AtomicBoolean hasLast = new AtomicBoolean(false);
            T lastValue;

            @Override
            protected boolean test(T data) throws Throwable {
                if (predicate.test(data)) {
                    lastValue = data;
                    hasLast.set(true);
                }
                return false;
            }

            @Override
            void onTerminated(Emitter<? super T> emitter, Throwable error) throws Throwable {
                if (hasLast.get()) {
                    emitter.data(lastValue);
                }
                super.onTerminated(emitter, error);
            }
        });
    }

    static <T> Flow<T> skip(Flow<T> upFlow, IFlow<?> timeoutFlow) {
        return create(emitter -> {
            Cancellable cancellable = timeoutFlow.asFlow().collectWith(emitter, CollectorHelper.get());
            upFlow.filter(it -> cancellable.isCancelled()).onStartCollect(emitter);
        });
    }

    static <T> Flow<T> skip(Flow<T> upFlow, int count) {
        return Flow.defer(() -> FlowFilter.filter(upFlow, Predicate.X.skip(count)));
    }

    static <T> Flow<T> skipAll(Flow<T> upFlow) {
        return FlowFilter.filter(upFlow, Predicate.X.alwaysFalse());
    }

    static <T> Flow<T> ifEmpty(Flow<T> upFlow, IFlow<T> fallback) {
        return Flow.defer(() -> new FlowFilter<T>(upFlow) {
            boolean isEmpty = true;

            @Override
            protected boolean test(T data) {
                isEmpty = false;
                return true;
            }

            @Override
            void onTerminated(Emitter<? super T> emitter, Throwable error) throws Throwable {
                if (error == null && isEmpty) {
                    fallback.asFlow().onStartCollect(emitter);
                    return;
                }
                super.onTerminated(emitter, error);
            }
        });
    }
}
