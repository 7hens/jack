package cn.thens.jack.tuple;

@SuppressWarnings("WeakerAccess")
public final class JTuple {
    private JTuple() throws InstantiationException {
        throw new InstantiationException();
    }

    //region methods

    public static <V1> JTuple1<V1>
    of(V1 v1) {
        return () -> v1;
    }

    public static <V1, V2> JTuple2<V1, V2>
    of(V1 v1, V2 v2) {
        return new T2<>(v1, v2);
    }

    public static <V1, V2, V3> JTuple3<V1, V2, V3>
    of(V1 v1, V2 v2, V3 v3) {
        return new T3<>(v1, v2, v3);
    }

    public static <V1, V2, V3, V4> JTuple4<V1, V2, V3, V4>
    of(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new T4<>(v1, v2, v3, v4);
    }

    public static <V1, V2, V3, V4, V5> JTuple5<V1, V2, V3, V4, V5>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
        return new T5<>(v1, v2, v3, v4, v5);
    }

    public static <V1, V2, V3, V4, V5, V6> JTuple6<V1, V2, V3, V4, V5, V6>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
        return new T6<>(v1, v2, v3, v4, v5, v6);
    }

    public static <V1, V2, V3, V4, V5, V6, V7> JTuple7<V1, V2, V3, V4, V5, V6, V7>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
        return new T7<>(v1, v2, v3, v4, v5, v6, v7);
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8> JTuple8<V1, V2, V3, V4, V5, V6, V7, V8>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
        return new T8<>(v1, v2, v3, v4, v5, v6, v7, v8);
    }

    public static <V1, V2, V3, V4, V5, V6, V7, V8, V9> JTuple9<V1, V2, V3, V4, V5, V6, V7, V8, V9>
    of(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9) {
        return new T9<>(v1, v2, v3, v4, v5, v6, v7, v8, v9);
    }

    //endregion

    //region classes

    private static class T1<V1> implements JTuple1<V1> {
        private final V1 v1;

        protected T1(V1 v1) {
            super();
            this.v1 = v1;
        }

        @Override
        public V1 v1() {
            return this.v1;
        }
    }

    private static class T2<V1, V2> extends T1<V1>
            implements JTuple2<V1, V2> {
        private final V2 v2;

        protected T2(V1 v1, V2 v2) {
            super(v1);
            this.v2 = v2;
        }

        @Override
        public V2 v2() {
            return this.v2;
        }
    }

    private static class T3<V1, V2, V3> extends T2<V1, V2>
            implements JTuple3<V1, V2, V3> {
        private final V3 v3;

        protected T3(V1 v1, V2 v2, V3 v3) {
            super(v1, v2);
            this.v3 = v3;
        }

        @Override
        public V3 v3() {
            return this.v3;
        }
    }

    private static class T4<V1, V2, V3, V4> extends T3<V1, V2, V3>
            implements JTuple4<V1, V2, V3, V4> {
        private final V4 v4;

        protected T4(V1 v1, V2 v2, V3 v3, V4 v4) {
            super(v1, v2, v3);
            this.v4 = v4;
        }

        @Override
        public V4 v4() {
            return this.v4;
        }
    }

    private static class T5<V1, V2, V3, V4, V5> extends T4<V1, V2, V3, V4>
            implements JTuple5<V1, V2, V3, V4, V5> {
        private final V5 v5;

        protected T5(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5) {
            super(v1, v2, v3, v4);
            this.v5 = v5;
        }

        @Override
        public V5 v5() {
            return this.v5;
        }
    }

    private static class T6<V1, V2, V3, V4, V5, V6> extends T5<V1, V2, V3, V4, V5>
            implements JTuple6<V1, V2, V3, V4, V5, V6> {
        private final V6 v6;

        protected T6(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6) {
            super(v1, v2, v3, v4, v5);
            this.v6 = v6;
        }

        @Override
        public V6 v6() {
            return this.v6;
        }
    }

    private static class T7<V1, V2, V3, V4, V5, V6, V7> extends T6<V1, V2, V3, V4, V5, V6>
            implements JTuple7<V1, V2, V3, V4, V5, V6, V7> {
        private final V7 v7;

        protected T7(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7) {
            super(v1, v2, v3, v4, v5, v6);
            this.v7 = v7;
        }

        @Override
        public V7 v7() {
            return this.v7;
        }
    }

    private static class T8<V1, V2, V3, V4, V5, V6, V7, V8> extends T7<V1, V2, V3, V4, V5, V6, V7>
            implements JTuple8<V1, V2, V3, V4, V5, V6, V7, V8> {
        private final V8 v8;

        protected T8(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8) {
            super(v1, v2, v3, v4, v5, v6, v7);
            this.v8 = v8;
        }

        @Override
        public V8 v8() {
            return this.v8;
        }
    }

    private static class T9<V1, V2, V3, V4, V5, V6, V7, V8, V9> extends T8<V1, V2, V3, V4, V5, V6, V7, V8>
            implements JTuple9<V1, V2, V3, V4, V5, V6, V7, V8, V9> {
        private final V9 v9;

        protected T9(V1 v1, V2 v2, V3 v3, V4 v4, V5 v5, V6 v6, V7 v7, V8 v8, V9 v9) {
            super(v1, v2, v3, v4, v5, v6, v7, v8);
            this.v9 = v9;
        }

        @Override
        public V9 v9() {
            return this.v9;
        }
    }

    //endregion
}
