package cn.thens.jack;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 7hens
 */
public final class PreferencesWrapper {
    public static Wrapper<SharedPreferences> wrap(SharedPreferences prefs) {
        return new Wrapper<>(prefs);
    }

    public static Wrapper<SharedPreferences> create(Context context, String name) {
        return wrap(context.getSharedPreferences(name, Context.MODE_PRIVATE));
    }

    public static abstract class Key<V> extends Wrapper.Key<SharedPreferences, V> {
    }

    public static Key<Integer> integerKey(String key) {
        return new Key<Integer>() {
            @Override
            protected void set(SharedPreferences target, Integer value) {
                target.edit().putInt(key, value).apply();
            }

            @Override
            protected Integer get(SharedPreferences target, Integer defaultValue) {
                return target.getInt(key, defaultValue);
            }
        };
    }
}
