package cn.thens.jack.util;

@SuppressWarnings("WeakerAccess")
public final class JTuple {
    public interface Base {
    }

    //region methods

    public static <V1> T1<V1>
    of(V1 v1) {
        return new T1<>(v1);
    }

    public static <V1, V2> T2<V1, V2>
    of(V1 v1, V2 v2) {
        return new T2<>(v1, v2);
    }

    public static <V1, V2, V3> T3<V1, V2, V3>
    of(V1 v1, V2 v2, V3 v3) {
        return new T3<>(v1, v2, v3);
    }

    public static <V1, V2, V3, V4> T4<V1, V2, V3, V4>
    of(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new T4<>(v1, v2, v3, v4);
    }

    public static <V1, V2, V3, V4, V5> T5<V1, V2, V3, V4, V5>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        return new T5<>(v1, v2, v3, v4, v5);
    }

    public static <V1, V2, V3, V4, V5, V6> T6<V1, V2, V3, V4, V5, V6>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        return new T6<>(v1, v2, v3, v4, v5, v6);
    }

    public static <V1, V2, V3, V4, V5, V6, V7> T7<V1, V2, V3, V4, V5, V6, V7>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
        return new T7<>(v1, v2, v3, v4, v5, v6, v7);
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8> T8<V1, V2, V3, V4, V5, V6, V7, V8>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
        return new T8<>(v1, v2, v3, v4, v5, v6, v7, v8);
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8, V9> T9<V1, V2, V3, V4, V5, V6, V7, V8, V9>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9) {
        return new T9<>(v1, v2, v3, v4, v5, v6, v7, v8, v9);
    }

    //endregion

    //region classes

    public static final class T1<V1> implements Base {
        public final V1 v1;

        protected T1(V1 v1) {
            this.v1 = v1;
        }
    }

    public static final class T2<V1, V2> implements Base {
        public final V1 v1;
        public final V2 v2;

        protected T2(V1 v1, V2 v2) {
            this.v1 = v1;
            this.v2 = v2;
        }
    }

    public static final class T3<V1, V2, V3> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;

        protected T3(V1 v1, V2 v2, V3 v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }
    }

    public static final class T4<V1, V2, V3, V4> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;
        public final V4 v4;

        protected T4(V1 v1, V2 v2, V3 v3, V4 v4) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
        }
    }

    public static final class T5<V1, V2, V3, V4, V5> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;
        public final V4 v4;
        public final V5 v5;

        protected T5(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
            this.v5 = v5;
        }
    }

    public static final class T6<V1, V2, V3, V4, V5, V6> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;
        public final V4 v4;
        public final V5 v5;
        public final V6 v6;

        protected T6(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
            this.v5 = v5;
            this.v6 = v6;
        }
    }

    public static final class T7<V1, V2, V3, V4, V5, V6, V7> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;
        public final V4 v4;
        public final V5 v5;
        public final V6 v6;
        public final V7 v7;

        protected T7(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
            this.v5 = v5;
            this.v6 = v6;
            this.v7 = v7;
        }
    }

    public static final class T8<V1, V2, V3, V4, V5, V6, V7, V8> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;
        public final V4 v4;
        public final V5 v5;
        public final V6 v6;
        public final V7 v7;
        public final V8 v8;

        protected T8(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
            this.v5 = v5;
            this.v6 = v6;
            this.v7 = v7;
            this.v8 = v8;
        }
    }

    public static final class T9<V1, V2, V3, V4, V5, V6, V7, V8, V9> implements Base {
        public final V1 v1;
        public final V2 v2;
        public final V3 v3;
        public final V4 v4;
        public final V5 v5;
        public final V6 v6;
        public final V7 v7;
        public final V8 v8;
        public final V9 v9;

        protected T9(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
            this.v5 = v5;
            this.v6 = v6;
            this.v7 = v7;
            this.v8 = v8;
            this.v9 = v9;
        }
    }

    //endregion

}
