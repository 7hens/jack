package cn.thens.jack.tuple;

public interface Tuple1<V1> extends Tuple {
    V1 v1();

    class X<V1> implements Tuple1<V1> {
        private final V1 v1;

        protected X(V1 v1) {
            this.v1 = v1;
        }

        @Override
        public V1 v1() {
            return this.v1;
        }
    }
}
