package cn.thens.jack.flow;

/**
 * @author 7hens
 */
public interface Collector<T> {
    void post(Reply<? extends T> reply) throws Throwable;
}
