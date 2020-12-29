package cn.thens.jack.app;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import cn.thens.jack.flow.Flow;

/**
 * @author 7hens
 */
public final class LifecycleFlow {
    public static Flow<Lifecycle.Event> from(final LifecycleOwner lifecycleOwner) {
        return Flow.create(emitter -> {
            LifecycleEventObserver observer = new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    emitter.next(event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        emitter.complete();
                    }
                }
            };
            lifecycleOwner.getLifecycle().addObserver(observer);
            emitter.addCancellable(() -> lifecycleOwner.getLifecycle().removeObserver(observer));
        });
    }

    public static Flow<Lifecycle.Event> mock(final View view) {
        return Flow.create(emitter -> {
            View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    emitter.next(Lifecycle.Event.ON_CREATE);
                    emitter.next(Lifecycle.Event.ON_START);
                    emitter.next(Lifecycle.Event.ON_RESUME);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    emitter.next(Lifecycle.Event.ON_PAUSE);
                    emitter.next(Lifecycle.Event.ON_STOP);
                    emitter.next(Lifecycle.Event.ON_DESTROY);
                    emitter.complete();
                }
            };
            if (ViewCompat.isAttachedToWindow(view)) {
                listener.onViewAttachedToWindow(view);
            }
            view.addOnAttachStateChangeListener(listener);
            emitter.addCancellable(() -> view.removeOnAttachStateChangeListener(listener));
        });
    }
}
