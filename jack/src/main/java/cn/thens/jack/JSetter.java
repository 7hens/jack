package cn.thens.jack;

@SuppressWarnings({"unused"})
public interface JSetter<T> extends JGetter<T> {
    void set(T t);
}
