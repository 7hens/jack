package cn.thens.jack.flow;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import cn.thens.jack.scheduler.CompositeCancellable;

/**
 * @author 7hens
 */
public class LifecycleFlow {
    public static Flow<Lifecycle.Event> from(final LifecycleOwner lifecycleOwner) {
        return Flow.create(emitter -> {
            LifecycleEventObserver observer = new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    emitter.data(event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        emitter.complete();
                    }
                }
            };
            lifecycleOwner.getLifecycle().addObserver(observer);
            emitter.addCancellable(new CompositeCancellable() {
                @Override
                protected void onCancel() {
                    super.onCancel();
                    lifecycleOwner.getLifecycle().removeObserver(observer);
                }
            });
        });
    }

    public static Flow<Lifecycle.Event> mock(final View view) {
        return Flow.create(emitter -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (view.isAttachedToWindow()) {
                    emitter.data(Lifecycle.Event.ON_CREATE);
                    emitter.data(Lifecycle.Event.ON_START);
                    emitter.data(Lifecycle.Event.ON_RESUME);
                }
            }
            View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    emitter.data(Lifecycle.Event.ON_CREATE);
                    emitter.data(Lifecycle.Event.ON_START);
                    emitter.data(Lifecycle.Event.ON_RESUME);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    emitter.data(Lifecycle.Event.ON_PAUSE);
                    emitter.data(Lifecycle.Event.ON_STOP);
                    emitter.data(Lifecycle.Event.ON_DESTROY);
                    emitter.complete();
                    view.removeOnAttachStateChangeListener(this);
                }
            };
            view.addOnAttachStateChangeListener(listener);
            emitter.addCancellable(new CompositeCancellable() {
                @Override
                protected void onCancel() {
                    super.onCancel();
                    view.removeOnAttachStateChangeListener(listener);
                }
            });
        });
    }
}
