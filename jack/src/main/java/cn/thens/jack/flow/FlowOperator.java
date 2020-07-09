package cn.thens.jack.flow;

public interface FlowOperator<Up, Dn> {
    Collector<? super Up> apply(Emitter<? super Dn> emitter) throws Throwable;
}
