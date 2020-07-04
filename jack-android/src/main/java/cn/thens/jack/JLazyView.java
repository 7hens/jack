package cn.thens.jack;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.view.View;
import android.view.Window;

import cn.thens.jack.func.Func0;
import cn.thens.jack.func.Func1;
import cn.thens.jack.property.Getter;
import cn.thens.jack.property.Lazy;

/**
 * @author 7hens
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class JLazyView {

    private final Func1<Integer, ? extends View> finder;

    private JLazyView(Func1<Integer, ? extends View> finder) {
        this.finder = finder;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> Getter<T> bind(final int id) {
        return Lazy.of(() -> (T) finder.invoke(id));
    }

    public static JLazyView create(Func1<Integer, ? extends View> finder) {
        return new JLazyView(finder);
    }

    public static JLazyView create(Func0<? extends View> view) {
        return create(id -> view.invoke().findViewById(id));
    }

    public static JLazyView create(final View view) {
        return create(view::findViewById);
    }

    public static JLazyView create(final Activity activity) {
        return create(activity::findViewById);
    }

    public static JLazyView create(final Fragment fragment) {
        return create(fragment::getView);
    }

    public static JLazyView create(final Dialog dialog) {
        return create(dialog::findViewById);
    }

    public static JLazyView create(final Window window) {
        return create(window::findViewById);
    }
}
