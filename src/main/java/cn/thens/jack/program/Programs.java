package cn.thens.jack.program;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.thens.jack.func.Values;

public final class Programs {
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static Application app;
    private static Plugins plugins;
    private static Program host;

    public static void initialize(Context context, File rootDir) {
        if (isInitialized.compareAndSet(false, true)) {
            app = (Application) context.getApplicationContext();
            plugins = new Plugins(app, rootDir);
            host = new AppProgram(app);
        }
    }

    private static void requireInitialized() {
        Values.require(isInitialized.get());
    }

    static Application app() {
        requireInitialized();
        return app;
    }

    public static Plugins plugins() {
        requireInitialized();
        return plugins;
    }

    public static Program host() {
        requireInitialized();
        return host;
    }
}
