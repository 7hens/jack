package cn.thens.jack.property;

@SuppressWarnings({"unused"})
public interface Setter<T> extends Getter<T> {
    void set(T t);
}
