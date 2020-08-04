package cn.thens.jack.flow;

public interface IFlow<T> {
    Flow<T> asFlow() throws Throwable;
}
