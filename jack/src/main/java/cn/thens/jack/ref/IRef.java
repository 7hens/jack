package cn.thens.jack.ref;

public interface IRef<T> {
    Ref<T> asRef() throws Throwable;
}
