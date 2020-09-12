package cn.thens.jack.app;

import android.content.SharedPreferences;

import java.util.Set;

import cn.thens.jack.func.Action3;
import cn.thens.jack.func.Func3;
import cn.thens.jack.ref.MutRefKey;

/**
 * @author 7hens
 */
public abstract class PreferencesKey<V> extends MutRefKey<SharedPreferences, V> {

    private static <V> PreferencesKey<V> create(
            String key,
            Func3<? super SharedPreferences, ? super String, ? super V, ? extends V> getter,
            Action3<? super SharedPreferences.Editor, ? super String, ? super V> setter) {
        return new PreferencesKey<V>() {
            @Override
            protected V get(SharedPreferences target, V defaultValue) throws Throwable {
                return getter.call(target, key, defaultValue);
            }

            @Override
            protected void set(SharedPreferences target, V value) throws Throwable {
                SharedPreferences.Editor editor = target.edit();
                setter.run(editor, key, value);
                editor.apply();
            }

            @Override
            protected boolean exists(SharedPreferences target) {
                return target.contains(key);
            }
        };
    }

    public static PreferencesKey<Boolean> bool(String key) {
        return create(key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean);
    }

    public static PreferencesKey<Integer> integer(String key) {
        return create(key, SharedPreferences::getInt, SharedPreferences.Editor::putInt);
    }

    public static PreferencesKey<Long> longX(String key) {
        return create(key, SharedPreferences::getLong, SharedPreferences.Editor::putLong);
    }

    public static PreferencesKey<Float> floatX(String key) {
        return create(key, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat);
    }

    public static PreferencesKey<String> string(String key) {
        return create(key, SharedPreferences::getString, SharedPreferences.Editor::putString);
    }

    public static PreferencesKey<Set<String>> stringSet(String key) {
        return create(key, SharedPreferences::getStringSet, SharedPreferences.Editor::putStringSet);
    }
}
