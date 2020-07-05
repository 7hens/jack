package cn.thens.jack.tuple;

public interface Tuple3<V1, V2, V3> extends Tuple2<V1, V2> {
    V3 v3();
}
