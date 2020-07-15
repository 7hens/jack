package cn.thens.jack.ref;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;

import cn.thens.jack.func.Action3;
import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Func3;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused"})
public abstract class BundleKey<V> extends MutRefKey<Bundle, V> {
    private static <V> BundleKey<V> create(
            String key,
            Func3<? super Bundle, ? super String, ? super V, ? extends V> getter,
            Action3<? super Bundle, ? super String, ? super V> setter) {
        return new BundleKey<V>() {
            @Override
            protected void set(Bundle target, V value) throws Throwable {
                setter.run(target, key, value);
            }

            @Override
            protected V get(Bundle target, V defaultValue) throws Throwable {
                return getter.call(target, key, defaultValue);
            }

            @Override
            protected boolean exists(Bundle target) throws Throwable {
                return target.containsKey(key);
            }
        };
    }

    private static <V> BundleKey<V> create(
            String key, V defaultValue,
            Func2<? super Bundle, ? super String, ? extends V> getter,
            Action3<? super Bundle, ? super String, ? super V> setter
    ) {
        return new BundleKey<V>() {
            @Override
            protected void set(Bundle target, V value) throws Throwable {
                setter.run(target, key, value);
            }

            @Override
            protected V get(Bundle target, V defaultValue) throws Throwable {
                V result = getter.call(target, key);
                return Ref.of(result).elvis(defaultValue);
            }

            @Override
            protected boolean exists(Bundle target) throws Throwable {
                return target.containsKey(key);
            }

            @Override
            protected V getDefaultValue() throws Throwable {
                return defaultValue;
            }
        };
    }


    public static BundleKey<CharSequence> charSequence(String key) {
        return create(key, Bundle::getCharSequence, Bundle::putCharSequence);
    }

    public static BundleKey<CharSequence[]> charSequenceArray(String key) {
        return create(key, new CharSequence[0], Bundle::getCharSequenceArray, Bundle::putCharSequenceArray);
    }

    public static BundleKey<ArrayList<CharSequence>> charSequenceList(String key) {
        return create(key, new ArrayList<>(), Bundle::getCharSequenceArrayList, Bundle::putCharSequenceArrayList);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static BundleKey<Integer> integer(String key) {
        return create(key, Bundle::getInt, Bundle::putInt);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static BundleKey<Long> longX(String key) {
        return create(key, Bundle::getLong, Bundle::putLong);
    }

    public static BundleKey<Float> floatX(String key) {
        return create(key, Bundle::getFloat, Bundle::putFloat);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static BundleKey<Double> doubleX(String key) {
        return create(key, Bundle::getDouble, Bundle::putDouble);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static BundleKey<Boolean> bool(String key) {
        return create(key, Bundle::getBoolean, Bundle::putBoolean);
    }

    public static BundleKey<Parcelable> parcelable(String key) {
        return create(key, null, Bundle::getParcelable, Bundle::putParcelable);
    }

    public static BundleKey<Serializable> serializable(String key) {
        return create(key, null, Bundle::getSerializable, Bundle::putSerializable);
    }
}
