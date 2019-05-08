package cn.thens.jack.func;

public interface JFunc3<P1, P2, P3, R> extends JFuncX<R> {
    R invoke(P1 p1, P2 p2, P3 p3);
}
