package cn.thens.jack.flow;

public interface Flowable<T> {
    Flow<T> asFlow() throws Throwable;
}
