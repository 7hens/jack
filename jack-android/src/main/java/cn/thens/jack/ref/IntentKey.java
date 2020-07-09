package cn.thens.jack.ref;

import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author 7hens
 */
public abstract class IntentKey<V> extends MutRefKey<Intent, V> {
    public static IntentKey<String> action() {
        return new IntentKey<String>() {
            @Override
            protected void set(Intent target, String value) {
                target.setAction(value);
            }

            @Override
            protected String get(Intent target, String defaultValue) {
                String result = target.getAction();
                return result != null ? result : defaultValue;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.getAction() != null;
            }
        };
    }

    public static IntentKey<String> string(String key) {
        return new IntentKey<String>() {
            @Override
            protected void set(Intent intent, String value) {
                intent.putExtra(key, value);
            }

            @Override
            protected String get(Intent intent, String defaultValue) {
                return Ref.of(intent.getStringExtra(key)).elvis(defaultValue);
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<String[]> stringArray(String key) {
        return new IntentKey<String[]>() {
            @Override
            protected void set(Intent intent, String[] value) {
                intent.putExtra(key, value);
            }

            @Override
            protected String[] get(Intent intent, String[] defaultValue) {
                String[] value = intent.getStringArrayExtra(key);
                return value != null ? value : defaultValue;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Integer> integer(String key) {
        return new IntentKey<Integer>() {
            @Override
            protected void set(Intent intent, Integer value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Integer get(Intent intent, Integer defaultValue) {
                return intent.getIntExtra(key, defaultValue);
            }

            @Override
            protected Integer getDefaultValue() {
                return 0;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Long> longValue(String key) {
        return new IntentKey<Long>() {
            @Override
            protected void set(Intent intent, Long value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Long get(Intent intent, Long defaultValue) {
                return intent.getLongExtra(key, defaultValue);
            }

            @Override
            protected Long getDefaultValue() {
                return 0L;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Float> floatValue(String key) {
        return new IntentKey<Float>() {
            @Override
            protected void set(Intent intent, Float value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Float get(Intent intent, Float defaultValue) {
                return intent.getFloatExtra(key, defaultValue);
            }

            @Override
            protected Float getDefaultValue() {
                return 0F;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Double> doubleValue(String key) {
        return new IntentKey<Double>() {
            @Override
            protected void set(Intent intent, Double value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Double get(Intent intent, Double defaultValue) {
                return intent.getDoubleExtra(key, defaultValue);
            }

            @Override
            protected Double getDefaultValue() {
                return 0.0;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Boolean> bool(String key) {
        return new IntentKey<Boolean>() {
            @Override
            protected void set(Intent intent, Boolean value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Boolean get(Intent intent, Boolean defaultValue) {
                return intent.getBooleanExtra(key, defaultValue);
            }

            @Override
            protected Boolean getDefaultValue() {
                return false;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Parcelable> parcelable(String key) {
        return new IntentKey<Parcelable>() {
            @Override
            protected void set(Intent intent, Parcelable value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Parcelable get(Intent intent, Parcelable defaultValue) {
                Parcelable value = intent.getParcelableExtra(key);
                return value != null ? value : defaultValue;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }

    public static IntentKey<Serializable> serializable(String key) {
        return new IntentKey<Serializable>() {
            @Override
            protected void set(Intent intent, Serializable value) {
                intent.putExtra(key, value);
            }

            @Override
            protected Serializable get(Intent intent, Serializable defaultValue) {
                Serializable value = intent.getSerializableExtra(key);
                return value != null ? value : defaultValue;
            }

            @Override
            protected boolean exists(Intent target) {
                return target.hasExtra(key);
            }
        };
    }
}
