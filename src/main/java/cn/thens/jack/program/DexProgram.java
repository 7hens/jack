package cn.thens.jack.program;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import cn.thens.jack.func.Lazy;

class DexProgram extends AbstractProgram {
    private final Context context;
    private final String packageName;
    private final DexInfo dexInfo;

    DexProgram(Context context, String packageName, DexInfo dexInfo) {
        this.context = context.getApplicationContext();
        this.packageName = packageName;
        this.dexInfo = dexInfo;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public DexInfo getDexInfo() {
        return dexInfo;
    }

    private final Lazy<PackageInfo> packageInfo = Lazy.of(() -> {
        return PackageInfoParser.parse(getContext(), getDexInfo());
    });

    @Override
    public PackageInfo getPackageInfo() {
        return packageInfo.get();
    }

    private final Lazy<PackageComponents> packageComponents = Lazy.of(() -> {
        return PackageComponentsParser.parse(getPackageInfo(), getResources().getAssets());
    });

    @Override
    public PackageComponents getPackageComponents() {
        return packageComponents.get();
    }

    private final Lazy<ClassLoader> classLoader = Lazy.of(() -> {
        ClassLoader classLoader = DexProgram.class.getClassLoader();
        return new PluginClassLoader(classLoader, getDexInfo());
    });

    @Override
    public ClassLoader getClassLoader() {
        return classLoader.get();
    }

    private final Lazy<Resources> resources = Lazy.of(() -> {
        Context context = getContext();
        PackageManager packageManager = context.getPackageManager();
        Resources resources = packageManager.getResourcesForApplication(getPackageInfo().applicationInfo);
        return new MergedResources(resources, context.getResources());
    });

    @Override
    public Resources getResources() {
        return resources.get();
    }

}
