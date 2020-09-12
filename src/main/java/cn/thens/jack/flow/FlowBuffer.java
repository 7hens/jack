package cn.thens.jack.flow;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author 7hens
 */
abstract class FlowBuffer<T> extends Flow<T> {
    private final Flow<T> upFlow;
    private final List<T> buffer = new CopyOnWriteArrayList<>();

    FlowBuffer(Flow<T> upFlow) {
        this.upFlow = upFlow;
    }

    @Override
    protected void onStartCollect(Emitter<? super T> emitter) throws Throwable {
        upFlow.collectWith(emitter, reply -> {
            if (reply.isTerminal()) {
                if (reply.error() == null) {
                    onComplete(emitter, buffer);
                }
                buffer.clear();
                emitter.post(reply);
                return;
            }
            buffer.add(reply.data());
            onBuffer(emitter, buffer);
        });
    }

    abstract void onBuffer(Emitter<? super T> emitter, List<T> buffer);

    abstract void onComplete(Emitter<? super T> emitter, List<T> buffer);

    static <T> Flow<T> takeLast(Flow<T> upFlow, int count) {
        return new FlowBuffer<T>(upFlow) {
            @Override
            void onBuffer(Emitter<? super T> emitter, List<T> buffer) {
                if (buffer.size() > count) {
                    buffer.remove(0);
                }
            }

            @Override
            void onComplete(Emitter<? super T> emitter, List<T> buffer) {
                if (count > 0) {
                    for (T data : buffer) {
                        emitter.next(data);
                    }
                }
            }
        };
    }

    static <T> Flow<T> lastElement(Flow<T> upFlow, int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("number should be greater than 0, " +
                    "but actual value is " + number);
        }
        return new FlowBuffer<T>(upFlow) {
            @Override
            void onBuffer(Emitter<? super T> emitter, List<T> buffer) {
                if (buffer.size() > number) {
                    buffer.remove(0);
                }
            }

            @Override
            void onComplete(Emitter<? super T> emitter, List<T> buffer) {
                if (buffer.size() < number) {
                    emitter.error(new NoSuchElementException());
                    return;
                }
                emitter.next(buffer.get(0));
            }
        };
    }

    static <T> Flow<T> skipLast(Flow<T> upFlow, int count) {
        return new FlowBuffer<T>(upFlow) {
            @Override
            void onBuffer(Emitter<? super T> emitter, List<T> buffer) {
                if (buffer.size() > count) {
                    emitter.next(buffer.remove(0));
                }
            }

            @Override
            void onComplete(Emitter<? super T> emitter, List<T> buffer) {
            }
        };
    }
}
