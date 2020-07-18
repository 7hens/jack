package cn.thens.jack.ref;

import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import cn.thens.jack.func.Action3;
import cn.thens.jack.func.Func2;
import cn.thens.jack.func.Func3;

/**
 * @author 7hens
 */
public abstract class IntentKey<V> extends MutRefKey<Intent, V> {
    private static <V> IntentKey<V> create(
            String key,
            Func3<? super Intent, ? super String, ? super V, ? extends V> getter,
            Action3<? super Intent, ? super String, ? super V> setter) {
        return new IntentKey<V>() {
            @Override
            protected void set(Intent target, V value) throws Throwable {
                setter.run(target, key, value);
            }

            @Override
            protected V get(Intent target, V defaultValue) throws Throwable {
                return getter.call(target, key, defaultValue);
            }

            @Override
            protected boolean exists(Intent target) throws Throwable {
                return target.hasExtra(key);
            }
        };
    }

    private static <V> IntentKey<V> create(
            String key,
            Func2<? super Intent, ? super String, ? extends V> getter,
            Action3<? super Intent, ? super String, ? super V> setter
    ) {
        return new IntentKey<V>() {
            @Override
            protected void set(Intent target, V value) throws Throwable {
                setter.run(target, key, value);
            }

            @Override
            protected V get(Intent target, V defaultValue) throws Throwable {
                V result = getter.call(target, key);
                return Ref.of(result).elvis(defaultValue);
            }

            @Override
            protected boolean exists(Intent target) throws Throwable {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<String> string(String key) {
        return create(key, Intent::getStringExtra, Intent::putExtra);
    }

    public static IntentKey<String[]> stringArray(String key) {
        return create(key, Intent::getStringArrayExtra, Intent::putExtra);
    }

    public static IntentKey<ArrayList<String>> stringList(String key) {
        return create(key, Intent::getStringArrayListExtra, Intent::putExtra);
    }

    public static IntentKey<CharSequence> charSequence(String key) {
        return create(key, Intent::getCharSequenceExtra, Intent::putExtra);
    }

    public static IntentKey<CharSequence[]> charSequenceArray(String key) {
        return create(key, Intent::getCharSequenceArrayExtra, Intent::putExtra);
    }

    public static IntentKey<ArrayList<CharSequence>> charSequenceList(String key) {
        return create(key, Intent::getCharSequenceArrayListExtra, Intent::putExtra);
    }

    public static IntentKey<Integer> integer(String key) {
        return create(key, Intent::getIntExtra, Intent::putExtra);
    }

    public static IntentKey<int[]> integerArray(String key) {
        return create(key, Intent::getIntArrayExtra, Intent::putExtra);
    }

    public static IntentKey<ArrayList<Integer>> integerList(String key) {
        return create(key, Intent::getIntegerArrayListExtra, Intent::putExtra);
    }

    public static IntentKey<Long> longX(String key) {
        return create(key, Intent::getLongExtra, Intent::putExtra);
    }

    public static IntentKey<long[]> longArray(String key) {
        return create(key, Intent::getLongArrayExtra, Intent::putExtra);
    }

    public static IntentKey<Float> floatX(String key) {
        return create(key, Intent::getFloatExtra, Intent::putExtra);
    }

    public static IntentKey<float[]> floatArray(String key) {
        return create(key, Intent::getFloatArrayExtra, Intent::putExtra);
    }

    public static IntentKey<Double> doubleValue(String key) {
        return create(key, Intent::getDoubleExtra, Intent::putExtra);
    }

    public static IntentKey<double[]> doubleArray(String key) {
        return create(key, Intent::getDoubleArrayExtra, Intent::putExtra);
    }

    public static IntentKey<Boolean> bool(String key) {
        return create(key, Intent::getBooleanExtra, Intent::putExtra);
    }

    public static IntentKey<boolean[]> boolArray(String key) {
        return create(key, Intent::getBooleanArrayExtra, Intent::putExtra);
    }

    public static IntentKey<Parcelable> parcelable(String key) {
        return create(key, Intent::getParcelableExtra, Intent::putExtra);
    }

    public static IntentKey<Parcelable[]> parcelableArray(String key) {
        return create(key, Intent::getParcelableArrayExtra, Intent::putExtra);
    }

    public static IntentKey<ArrayList<Parcelable>> parcelableList(String key) {
        return create(key, Intent::getParcelableArrayListExtra, Intent::putExtra);
    }

    public static IntentKey<Serializable> serializable(String key) {
        return create(key, Intent::getSerializableExtra, Intent::putExtra);
    }
}
