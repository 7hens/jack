package cn.thens.jack.ref;

@SuppressWarnings({"unused"})
public interface Setter<T> extends Getter<T> {
    void set(T t);
}
