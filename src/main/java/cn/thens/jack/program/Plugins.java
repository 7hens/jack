package cn.thens.jack.program;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.thens.jack.func.Values;

public final class Plugins {
    private static final String BASK_APK = "base.jar";

    private final Context context;
    private final Map<String, Program> plugins = new ConcurrentHashMap<>();
    private final File rootDir;

    Plugins(Context context, File rootDir) {
        this.context = context.getApplicationContext();
        this.rootDir = Utils.dir(rootDir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Program install(File apk) {
        try {
            String apkPath = apk.getAbsolutePath();
            PackageManager packageManager = context.getPackageManager();

            if (!apk.exists() || !apk.isFile()) {
                throw new FileNotFoundException("install error, file($apkPath) not exists");
            }

            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath, 0);
            if (packageInfo == null) {
                throw new IOException("install error, packageInfo is null, apkPath: " + apkPath);
            }
            String packageName = packageInfo.packageName;
            File programDir = Utils.dir(new File(rootDir, packageName));
            File newApk = new File(programDir, BASK_APK);
            if (!newApk.exists()) newApk.createNewFile();
            String newApkPath = newApk.getAbsolutePath();
            boolean isNewApk = !apkPath.equals(newApkPath);
            boolean isSameApk = !isNewApk || apk.length() == newApk.length();
            if (plugins.containsKey(packageName) && isSameApk) {
                return plugins.get(packageName);
            }
            if (isNewApk) Utils.copyTo(apk, newApk);
            Program program = new DexProgram(context, packageName, DexInstaller.install(newApk, isNewApk));
            plugins.put(packageName, program);
            return program;
        } catch (Throwable e) {
            throw Values.wrap(e);
        }
    }

    public void installAll() {
        File[] programDirs = rootDir.listFiles();
        if (programDirs == null) programDirs = new File[0];
        try {
            for (File programDir : programDirs) {
                String packageName = programDir.getName();
                File apkFile = new File(programDir, BASK_APK);
                if (!(apkFile.exists() && apkFile.isFile() && apkFile.length() > 0L)) {
                    Utils.delete(programDir);
                    continue;
                }
                DexInfo dexInfo = DexInstaller.install(apkFile, false);
                plugins.put(packageName, new DexProgram(context, packageName, dexInfo));
            }
        } catch (Throwable e) {
            throw Values.wrap(e);
        }
    }

    public boolean uninstall(String packageName) {
        File programDir = new File(rootDir, packageName);
        plugins.remove(packageName);
        if (!programDir.exists()) return true;
        return Utils.delete(programDir);
    }

    public boolean uninstallAll() {
        boolean result = true;
        File[] programDirs = rootDir.listFiles();
        if (programDirs == null) programDirs = new File[0];
        for (File programDir : programDirs) {
            plugins.remove(programDir.getName());
            result = result && Utils.delete(programDir);
        }
        return result;
    }

    public Program get(String packageName) {
        return plugins.get(packageName);
    }

    public Map<String, Program> all() {
        return Collections.unmodifiableMap(plugins);
    }

}
