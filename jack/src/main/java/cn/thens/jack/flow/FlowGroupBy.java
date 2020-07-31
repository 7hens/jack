package cn.thens.jack.flow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.thens.jack.func.Func1;
import cn.thens.jack.scheduler.Cancellable;
import cn.thens.jack.scheduler.IScheduler;

/**
 * @author 7hens
 */
class FlowGroupBy<K, V> extends MapFlow<K, V> {
    private final Flow<V> upFlow;
    private final Func1<? super V, ? extends K> keySelector;

    FlowGroupBy(Flow<V> upFlow, Func1<? super V, ? extends K> keySelector) {
        this.upFlow = upFlow;
        this.keySelector = keySelector;
    }

    @Override
    protected Cancellable collect(IScheduler scheduler, Collector<? super Entry<K, V>> collector) {
        CollectorEmitter<? super Entry<K, V>> emitter = CollectorEmitter.create(scheduler, collector);
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

    protected void onStart(CollectorEmitter<? super Entry<K, V>> emitter) throws Throwable {
        final Map<K, FlowEmitter<V>> map = new ConcurrentHashMap<>();
        upFlow.collect(emitter, reply -> {
            if (reply.isTerminal()) {
                Reply<V> entryReply = reply.newReply(null);
                for (FlowEmitter<? super V> entryEmitter : map.values()) {
                    entryEmitter.post(entryReply);
                }
                emitter.post(reply.newReply(null));
                return;
            }
            try {
                V value = reply.data();
                K key = keySelector.call(value);
                FlowEmitter<V> entryEmitter = map.get(key);
                if (entryEmitter == null) {
                    entryEmitter = FlowEmitter.publish();
                    map.put(key, entryEmitter);
                    emitter.data(newEntry(key, entryEmitter));
                }
                entryEmitter.data(value);
            } catch (Throwable e) {
                emitter.error(e);
            }
        });
    }

    private Entry<K, V> newEntry(K key, FlowEmitter<V> emitter) {
        return new Entry<K, V>(key) {
            @Override
            protected Cancellable collect(IScheduler scheduler, Collector<? super V> collector) {
                return emitter.asFlow().collect(scheduler, collector);
            }
        };
    }
}
