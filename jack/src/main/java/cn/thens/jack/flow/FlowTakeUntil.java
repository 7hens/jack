package cn.thens.jack.flow;


import cn.thens.jack.func.Predicate;

/**
 * @author 7hens
 */
abstract class FlowTakeUntil<T> implements FlowOperator<T, T> {
    @Override
    public Collector<T> apply(Emitter<? super T> emitter) {
        return new Collector<T>() {
            @Override
            public void onCollect(Reply<? extends T> reply) {
                if (reply.isTerminal()) {
                    emitter.emit(reply);
                    return;
                }
                try {
                    emitter.emit(reply);
                    if (test(reply.data())) {
                        emitter.complete();
                    }
                } catch (Throwable e) {
                    emitter.error(e);
                }
            }
        };
    }

    protected abstract boolean test(T data) throws Throwable;

    static <T> FlowTakeUntil<T> takeUntil(Predicate<? super T> predicate) {
        return new FlowTakeUntil<T>() {
            @Override
            protected boolean test(T data) throws Throwable {
                return predicate.test(data);
            }
        };
    }

    static <T> FlowTakeUntil<T> takeUntil(T t) {
        return new FlowTakeUntil<T>() {
            @Override
            protected boolean test(T data) throws Throwable {
                return data == t;
            }
        };
    }

    static <T> FlowOperator<T, T> take(int count) {
        if (count <= 0) {
            return emitter -> {
                emitter.complete();
                return CollectorHelper.from(emitter);
            };
        }
        return new FlowTakeUntil<T>() {
            private Predicate.X<T> predicate;

            @Override
            public Collector<T> apply(Emitter<? super T> emitter) {
                predicate = Predicate.X.skip(count - 1);
                return super.apply(emitter);
            }

            @Override
            protected boolean test(T data) throws Throwable {
                return predicate.test(data);
            }
        };
    }
}
