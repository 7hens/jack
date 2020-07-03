package cn.thens.jack;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author 7hens
 */
@SuppressWarnings({"unused"})
public abstract class BundleKey<V> extends Wrapper.Key<Bundle, V> {

    public static BundleKey<String> string(String key) {
        return new BundleKey<String>() {
            @Override
            protected void set(Bundle bundle, String value) {
                bundle.putString(key, value);
            }

            @Override
            protected String get(Bundle bundle, String defaultValue) {
                return bundle.getString(key, defaultValue);
            }
        };
    }

    public static BundleKey<String[]> stringArray(String key) {
        return new BundleKey<String[]>() {
            @Override
            protected void set(Bundle bundle, String[] value) {
                bundle.putStringArray(key, value);
            }

            @Override
            protected String[] get(Bundle bundle, String[] defaultValue) {
                String[] value = bundle.getStringArray(key);
                return value != null ? value : defaultValue;
            }
        };
    }

    public static BundleKey<Integer> integer(String key) {
        return new BundleKey<Integer>() {
            @Override
            protected void set(Bundle bundle, Integer value) {
                bundle.putInt(key, value);
            }

            @Override
            protected Integer get(Bundle bundle, Integer defaultValue) {
                return bundle.getInt(key, defaultValue);
            }

            @Override
            protected Integer getDefaultValue() {
                return 0;
            }
        };
    }

    public static BundleKey<Long> longValue(String key) {
        return new BundleKey<Long>() {
            @Override
            protected void set(Bundle bundle, Long value) {
                bundle.putLong(key, value);
            }

            @Override
            protected Long get(Bundle bundle, Long defaultValue) {
                return bundle.getLong(key, defaultValue);
            }

            @Override
            protected Long getDefaultValue() {
                return 0L;
            }
        };
    }

    public static BundleKey<Float> floatValue(String key) {
        return new BundleKey<Float>() {
            @Override
            protected void set(Bundle bundle, Float value) {
                bundle.putFloat(key, value);
            }

            @Override
            protected Float get(Bundle bundle, Float defaultValue) {
                return bundle.getFloat(key, defaultValue);
            }

            @Override
            protected Float getDefaultValue() {
                return 0F;
            }
        };
    }

    public static BundleKey<Double> doubleValue(String key) {
        return new BundleKey<Double>() {
            @Override
            protected void set(Bundle bundle, Double value) {
                bundle.putDouble(key, value);
            }

            @Override
            protected Double get(Bundle bundle, Double defaultValue) {
                return bundle.getDouble(key, defaultValue);
            }

            @Override
            protected Double getDefaultValue() {
                return 0.0;
            }
        };
    }

    public static BundleKey<Boolean> bool(String key) {
        return new BundleKey<Boolean>() {
            @Override
            protected void set(Bundle bundle, Boolean value) {
                bundle.putBoolean(key, value);
            }

            @Override
            protected Boolean get(Bundle bundle, Boolean defaultValue) {
                return bundle.getBoolean(key, defaultValue);
            }

            @Override
            protected Boolean getDefaultValue() {
                return false;
            }
        };
    }

    public static BundleKey<Parcelable> parcelable(String key) {
        return new BundleKey<Parcelable>() {
            @Override
            protected void set(Bundle bundle, Parcelable value) {
                bundle.putParcelable(key, value);
            }

            @Override
            protected Parcelable get(Bundle bundle, Parcelable defaultValue) {
                Parcelable value = bundle.getParcelable(key);
                return value != null ? value : defaultValue;
            }
        };
    }

    public static BundleKey<Serializable> serializable(String key) {
        return new BundleKey<Serializable>() {
            @Override
            protected void set(Bundle bundle, Serializable value) {
                bundle.putSerializable(key, value);
            }

            @Override
            protected Serializable get(Bundle bundle, Serializable defaultValue) {
                Serializable value = bundle.getSerializable(key);
                return value != null ? value : defaultValue;
            }
        };
    }
}
