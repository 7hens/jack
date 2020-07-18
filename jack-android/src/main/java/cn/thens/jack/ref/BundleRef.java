package cn.thens.jack.ref;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public abstract class BundleRef extends Ref<Bundle> {
    public static BundleRef of(Bundle bundle) {
        return new BundleRef() {
            @Override
            public Bundle get() {
                return bundle;
            }
        };
    }

    public static BundleRef of(Fragment fragment) {
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
            fragment.setArguments(args);
        }
        return of(args);
    }

    public static BundleRef create() {
        return of(new Bundle());
    }

    public static BundleRef self(Ref<Bundle> ref) {
        return of(ref.get());
    }

    @Override
    public <V> BundleRef put(MutRefKey<Bundle, V> key, V value) {
        super.put(key, value);
        return this;
    }

    @Override
    public <V> BundleRef putIfAbsent(MutRefKey<Bundle, V> key, V value) {
        super.putIfAbsent(key, value);
        return this;
    }
}
