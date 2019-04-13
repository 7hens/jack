package cn.thens.jack;

@SuppressWarnings({"WeakerAccess", "unused"})
public interface JAction {

    interface P0 extends JAction {
        void invoke();
    }

    interface P1<P1> extends JAction {
        void invoke(P1 p1);
    }

    interface P2<P1, P2> extends JAction {
        void invoke(P1 p1, P2 p2);
    }

    interface P3<P1, P2, P3> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3);
    }

    interface P4<P1, P2, P3, P4> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4);
    }

    interface P5<P1, P2, P3, P4, P5> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
    }

    interface P6<P1, P2, P3, P4, P5, P6> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
    }

    interface P7<P1, P2, P3, P4, P5, P6, P7> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
    }

    interface P8<P1, P2, P3, P4, P5, P6, P7, P8> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
    }

    interface P9<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends JAction {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);
    }

    interface Pn extends JAction {
        void invoke(Object... params);
    }
}
