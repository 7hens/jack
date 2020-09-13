package cn.thens.jack.program;

import java.net.URL;

/**
 * @author 7hens
 */
public class PluginClassLoader extends DexInfo.ClassLoader {
    public PluginClassLoader(ClassLoader parent, DexInfo dexInfo) {
        super(parent, dexInfo);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) return clazz;
        try {
            return findClass(name);
        } catch (Throwable e) {
            return super.loadClass(name, resolve);
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        return url != null ? url : super.getResource(name);
    }
}
