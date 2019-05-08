package cn.thens.jack;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.view.View;
import android.view.Window;

import cn.thens.jack.func.JFunc0;
import cn.thens.jack.func.JFunc1;
import cn.thens.jack.property.JGetter;
import cn.thens.jack.property.JLazy;

/**
 * @author 7hens
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class JLazyView {

    private final JFunc1<Integer, ? extends View> finder;

    private JLazyView(JFunc1<Integer, ? extends View> finder) {
        this.finder = finder;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> JGetter<T> bind(final int id) {
        return JLazy.create(() -> (T) finder.invoke(id));
    }

    public static JLazyView create(JFunc1<Integer, ? extends View> finder) {
        return new JLazyView(finder);
    }

    public static JLazyView create(JFunc0<? extends View> view) {
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
