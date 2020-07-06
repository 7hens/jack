package cn.thens.jack.ref;

import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Functions;

/**
 * @author 7hens
 */
public class MutableRef<T> extends Ref<T> {
    private Ref<T> ref;

    MutableRef(Ref<T> ref) {
        this.ref = ref;
    }

    @Override
    public Ref<T> asRef() {
        return ref;
    }

    @Override
    public T get() {
        return ref.get();
    }

    public final MutableRef<T> set(T newValue) {
        return set(Ref.of(newValue));
    }

    public MutableRef<T> set(Ref<T> ref) {
        this.ref = ref;
        return this;
    }

    public MutableRef<T> observe(Action2<? super T, ? super T> action) {
        Action2.X<? super T, ? super T> actionX = Functions.of(action);
        return new MutableRef<T>(this) {
            @Override
            public MutableRef<T> set(Ref<T> ref) {
                actionX.run(ref.get(), super.get());
                return super.set(ref);
            }
        };
    }
}
