package cn.thens.jack.property;

@SuppressWarnings({"unused"})
public interface JSetter<T> extends JGetter<T> {
    void set(T t);
}
