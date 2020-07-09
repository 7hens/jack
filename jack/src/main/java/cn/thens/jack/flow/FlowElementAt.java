package cn.thens.jack.flow;


import java.util.NoSuchElementException;

import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
abstract class FlowElementAt<T> implements FlowOperator<T, T> {
    @Override
    public Collector<T> apply(Emitter<? super T> emitter) {
        return reply -> {
            if (reply.isTerminal()) {
                emitter.error(new NoSuchElementException());
                return;
            }
            try {
                if (test(reply.data())) {
                    emitter.emit(reply);
                    emitter.complete();
                }
            } catch (Throwable e) {
                emitter.error(e);
            }
        };
    }

    abstract boolean test(T data) throws Throwable;

    static <T> FlowElementAt<T> first(final Predicate<? super T> predicate) {
        return new FlowElementAt<T>() {
            @Override
            boolean test(T data) throws Throwable {
                return predicate.test(data);
            }
        };
    }

    static <T> FlowElementAt<T> first() {
        return first(Predicate.X.alwaysTrue());
    }

    static <T> FlowElementAt<T> elementAt(int index) {
        return first(Predicate.X.skip(index));
    }
}
