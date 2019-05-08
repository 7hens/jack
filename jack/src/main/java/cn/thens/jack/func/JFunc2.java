package cn.thens.jack.func;

public interface JFunc2<P1, P2, R> extends JFuncX<R> {
    R invoke(P1 p1, P2 p2);
}
