package cn.thens.jack.ref;

import cn.thens.jack.func.Action2;
import cn.thens.jack.func.Actions;

/**
 * @author 7hens
 */
public class MutRef<T> extends Ref<T> {
    private Ref<T> ref;

    MutRef(Ref<T> ref) {
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

    public final MutRef<T> set(T newValue) {
        return set(Ref.of(newValue));
    }

    public MutRef<T> set(Ref<T> ref) {
        this.ref = ref;
        return this;
    }

    public MutRef<T> observe(Action2<? super T, ? super T> action) {
        Action2.X<? super T, ? super T> actionX = Actions.of(action);
        return new MutRef<T>(this) {
            @Override
            public MutRef<T> set(Ref<T> ref) {
                actionX.run(ref.get(), super.get());
                return super.set(ref);
            }
        };
    }
}
