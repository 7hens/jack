package cn.thens.jack.program;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;

import cn.thens.jack.func.Lazy;

class AppProgram extends AbstractProgram {
    private final Context context;

    AppProgram(Context context) {
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public String getPackageName() {
        return context.getPackageName();
    }

    private final Lazy<PackageInfo> packageInfo = Lazy.of(() -> {
        return PackageInfoParser.parse(getContext());
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

    @Override
    public ClassLoader getClassLoader() {
        return getContext().getClassLoader();
    }

    @Override
    public Resources getResources() {
        return getContext().getResources();
    }

    private final Lazy<DexInfo> dexInfo = Lazy.of(() -> {
        ApplicationInfo applicationInfo = getContext().getApplicationInfo();
        return new DexInfo(applicationInfo.sourceDir, "", applicationInfo.nativeLibraryDir);
    });

    @Override
    public DexInfo getDexInfo() {
        return dexInfo.get();
    }
}
