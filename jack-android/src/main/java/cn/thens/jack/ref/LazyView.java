package cn.thens.jack.ref;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.Fragment;

import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;

/**
 * @author 7hens
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class LazyView {

    private final Func1<Integer, ? extends View> finder;

    private LazyView(Func1<Integer, ? extends View> finder) {
        this.finder = finder;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> Ref<T> bind(final int id) {
        return Ref.lazy(() -> (T) finder.call(id));
    }

    public static LazyView create(Func1<Integer, ? extends View> finder) {
        return new LazyView(finder);
    }

    public static LazyView create(Func0<? extends View> view) {
        return create(id -> view.call().findViewById(id));
    }

    public static LazyView create(final View view) {
        return create(view::findViewById);
    }

    public static LazyView create(final Activity activity) {
        return create(activity::findViewById);
    }

    public static LazyView create(final Fragment fragment) {
        return create(fragment::getView);
    }

    public static LazyView create(final Dialog dialog) {
        return create(dialog::findViewById);
    }

    public static LazyView create(final Window window) {
        return create(window::findViewById);
    }
}
