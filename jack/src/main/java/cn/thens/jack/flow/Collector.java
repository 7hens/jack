package cn.thens.jack.flow;

/**
 * @author 7hens
 */
public interface Collector<T> {
    void onCollect(Reply<? extends T> reply);
}
