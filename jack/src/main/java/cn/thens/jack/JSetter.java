package cn.thens.jack;

@SuppressWarnings({"WeakerAccess", "unused"})
public interface JSetter<T> extends JGetter<T> {
    void set(T t);
}
