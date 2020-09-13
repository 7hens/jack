package cn.thens.jack.program;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

import java.io.File;

class PackageInfoParser {

    private static final int FLAG_GET_SIGNING_CERTIFICATES =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    ? PackageManager.GET_SIGNING_CERTIFICATES
                    : PackageManager.GET_SIGNATURES;

    private static final int FLAG_MATCH_UNINSTALLED_PACKAGES =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? PackageManager.MATCH_UNINSTALLED_PACKAGES
                    : PackageManager.GET_UNINSTALLED_PACKAGES;

    private static final int FLAG_MATCH_DISABLED_COMPONENTS =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? PackageManager.MATCH_DISABLED_COMPONENTS
                    : PackageManager.GET_DISABLED_COMPONENTS;

    private static final int FLAG_MATCH_DISABLED_UNTIL_USED_COMPONENTS =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
                    : (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    ? PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS
                    : 0);

    @SuppressWarnings("PointlessBitwiseExpression")
    private static final int packageInfoFlags = 0 |
            PackageManager.GET_ACTIVITIES |
            PackageManager.GET_SERVICES |
            PackageManager.GET_PROVIDERS |
            PackageManager.GET_RECEIVERS |
            PackageManager.GET_META_DATA |
            PackageManager.GET_INTENT_FILTERS |
            PackageManager.GET_URI_PERMISSION_PATTERNS |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_PERMISSIONS |
            PackageManager.GET_INSTRUMENTATION |
            FLAG_GET_SIGNING_CERTIFICATES |
            FLAG_MATCH_DISABLED_COMPONENTS |
            FLAG_MATCH_DISABLED_UNTIL_USED_COMPONENTS |
            FLAG_MATCH_UNINSTALLED_PACKAGES;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static PackageInfo parse(Context context, DexInfo dexInfo) {
        PackageManager packageManager = context.getPackageManager();
        String apkPath = dexInfo.getDexPath();
        PackageInfo packageInfo = check(packageManager.getPackageArchiveInfo(apkPath, packageInfoFlags));
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        applicationInfo.sourceDir = apkPath;
        applicationInfo.publicSourceDir = apkPath;
        applicationInfo.nativeLibraryDir = dexInfo.getLibraryDirectory();

        File parentDir = new File(apkPath).getParentFile();
        File dataDir = new File(parentDir, "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        applicationInfo.dataDir = dataDir.getAbsolutePath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applicationInfo.deviceProtectedDataDir = dataDir.getAbsolutePath();
        }
        return packageInfo;
    }

    public static PackageInfo parse(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        return check(packageManager.getPackageInfo(context.getPackageName(),
                packageInfoFlags & ~FLAG_GET_SIGNING_CERTIFICATES));
    }

    private static PackageInfo check(PackageInfo it) {
        if (it == null) throw new NullPointerException();
        if (it.activities == null) it.activities = new ActivityInfo[0];
        if (it.services == null) it.services = new ServiceInfo[0];
        if (it.receivers == null) it.receivers = new ActivityInfo[0];
        if (it.providers == null) it.providers = new ProviderInfo[0];
        return it;
    }
}
