package cn.thens.jack.flow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.thens.jack.func.Func1;

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

    protected void onStartCollect(Emitter<? super Entry<K, V>> emitter) throws Throwable {
        final Map<K, FlowEmitter<V>> map = new ConcurrentHashMap<>();
        upFlow.collectWith(emitter, reply -> {
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
                    emitter.next(newEntry(key, entryEmitter));
                }
                entryEmitter.next(value);
            } catch (Throwable e) {
                emitter.error(e);
            }
        });
    }

    private Entry<K, V> newEntry(K key, FlowEmitter<V> flowEmitter) {
        return new Entry<K, V>(key) {
            @Override
            protected void onStartCollect(Emitter<? super V> emitter) throws Throwable {
                flowEmitter.asFlow().onStartCollect(emitter);
            }
        };
    }
}
