package cn.thens.jack.program;

import android.content.pm.PackageInfo;
import android.content.res.Resources;

public interface Program {
    String getPackageName();

    PackageInfo getPackageInfo();

    PackageComponents getPackageComponents();

    ClassLoader getClassLoader();

    Resources getResources();

    DexInfo getDexInfo();
}
