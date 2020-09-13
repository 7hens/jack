package cn.thens.jack.program;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.thens.jack.func.Lazy;

/**
 * @author 7hens
 */
public final class DexInstaller {
    private static final String BASE_DEX = "base.dex";
    private static final String LIB_DIR = "lib";
    private static final String OAT_DIR = "oat";
    private static final String ODEX_DIR = "odex";
    private static final String CPU_ARMEABI = "armeabi";

    public static DexInfo install(File apk, boolean isNewApk) throws IOException {
        File appDir = apk.getParentFile();
        File libDir = Utils.dir(new File(appDir, LIB_DIR));
        File odexDir = Utils.dir(new File(appDir, isArt.get() ? OAT_DIR : ODEX_DIR));
        File baseDex = new File(odexDir, BASE_DEX);
        if (isNewApk) installNativeBinaries(new ZipFile(apk), libDir);
        return new DexInfo(apk.getAbsolutePath(), odexDir.getAbsolutePath(), libDir.getAbsolutePath());
    }

    private static void installNativeBinaries(ZipFile zip, File libDir) throws IOException {
        String[] abis = supportedAbis.get();
        StringBuilder debugText = new StringBuilder(Arrays.toString(abis) + "\n");
        Map<String, Map<String, ZipEntry>> mappedLibEntries = getMappedLibEntries(zip);
        for (Map.Entry<String, Map<String, ZipEntry>> entry : mappedLibEntries.entrySet()) {
            String soName = entry.getKey();
            Map<String, ZipEntry> entryMap = entry.getValue();
            for (String abi : abis) {
                if (entryMap.containsKey(abi)) {
                    ZipEntry zipEntry = entryMap.get(abi);
                    if (zipEntry != null && zipEntry.getSize() > 0) {
                        File soFile = new File(libDir, soName);
                        if (!soFile.exists() || soFile.length() != zipEntry.getSize()) {
                            InputStream input = zip.getInputStream(zipEntry);
                            OutputStream output = new FileOutputStream(soFile);
                            Utils.copyTo(input, output);
                        }
                        debugText.append("copy ${zipEntry.name}\n");
                        break;
                    }
                }
            }
        }
    }

    /**
     * mapOf("libxx.so" to mapOf("abi" to zipEntry))
     */
    private static Map<String, Map<String, ZipEntry>> getMappedLibEntries(ZipFile zip) {
        Map<String, Map<String, ZipEntry>> libEntries = new HashMap<>();
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryPath = entry.getName();
            if (entryPath.contains("../") || entry.isDirectory()) continue;
            if (entryPath.startsWith("lib/") && entryPath.endsWith(".so")) {
                int firstIndexOfSlash = entryPath.indexOf("/");
                int lastIndexOfSlash = entryPath.lastIndexOf("/");
                String abi = entryPath.substring(firstIndexOfSlash + 1, lastIndexOfSlash);
                String soName = entryPath.substring(lastIndexOfSlash + 1);
                Map<String, ZipEntry> map = libEntries.get(soName);
                if (map == null) {
                    map = new HashMap<>();
                    libEntries.put(soName, map);
                }
                map.put(abi, entry);
            }
        }
        return libEntries;
    }

    private static final String[] DEFAULT_SUPPORTED_ABIS =
            new String[]{Build.CPU_ABI, Build.CPU_ABI2, CPU_ARMEABI};

    private static Lazy<String[]> supportedAbis = Lazy.of(() -> {
        Set<String> result = new LinkedHashSet<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = is64Bit() ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
            result.addAll(Arrays.asList(abis));
        }
        result.addAll(Arrays.asList(DEFAULT_SUPPORTED_ABIS));
        return result.toArray(new String[0]);
    });

    @SuppressLint("DiscouragedPrivateApi")
    private static boolean is64Bit() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        try {
            Class<?> cVMRuntime = Class.forName("dalvik.system.VMRuntime");
            Object runtime = cVMRuntime.getDeclaredMethod("getRuntime").invoke(null);
            return (boolean) cVMRuntime.getDeclaredMethod("is64Bit").invoke(runtime);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Lazy<Boolean> isArt = Lazy.of(() -> {
        String property = System.getProperty("java.vm.version", "");
        return property != null && property.startsWith("2");
    });
}
