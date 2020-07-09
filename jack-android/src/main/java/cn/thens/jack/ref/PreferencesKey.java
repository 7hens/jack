package cn.thens.jack.ref;

import android.content.SharedPreferences;

/**
 * @author 7hens
 */
public abstract class PreferencesKey<V> extends MutRefKey<SharedPreferences, V> {
    public static PreferencesKey<Integer> integer(String key) {
        return new PreferencesKey<Integer>() {
            @Override
            protected void set(SharedPreferences target, Integer value) {
                target.edit().putInt(key, value).apply();
            }

            @Override
            protected Integer get(SharedPreferences target, Integer defaultValue) {
                return target.getInt(key, defaultValue);
            }

            @Override
            protected boolean exists(SharedPreferences target) {
                return target.contains(key);
            }
        };
    }
}
