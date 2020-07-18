package cn.thens.jack.ref;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.CancellationException;

import cn.thens.jack.app.ActivityRequest;
import cn.thens.jack.flow.Flow;
import cn.thens.jack.func.Func1;

public abstract class IntentRef extends Ref<Intent> {
    public static IntentRef of(Intent intent) {
        return new IntentRef() {
            @Override
            public Intent get() {
                return intent;
            }
        };
    }

    public static IntentRef of(Activity activity) {
        return of(activity.getIntent());
    }

    public static IntentRef create() {
        return of(new Intent());
    }

    public static IntentRef create(Context context, Class<?> cls) {
        return of(new Intent(context, cls));
    }

    public static IntentRef create(String action) {
        return of(new Intent(action));
    }

    public static Func1<Ref<Intent>, IntentRef> self() {
        return ref -> of(ref.get());
    }

    @Override
    public <V> IntentRef put(MutRefKey<Intent, V> key, V value) {
        super.put(key, value);
        return this;
    }

    @Override
    public <V> IntentRef putIfAbsent(MutRefKey<Intent, V> key, V value) {
        super.putIfAbsent(key, value);
        return this;
    }

    public Flow<Intent> startForResult(Context context, Bundle options) {
        return ActivityRequest.with(context)
                .startForResult(get(), options)
                .map(result -> {
                    int code = result.getCode();
                    switch (code) {
                        case Activity.RESULT_CANCELED:
                            throw new CancellationException();
                        case Activity.RESULT_OK:
                            return result.getData();
                        default:
                            throw new IllegalArgumentException("unsupported result code: " + code);
                    }
                });
    }

    public Flow<Intent> startForResult(Context context) {
        return startForResult(context, null);
    }
}
