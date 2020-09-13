package cn.thens.jack.program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Utils {
    public static File dir(File file) {
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file;
    }

    public static boolean delete(File dir) {
        if (!dir.exists()) return false;
        boolean result = true;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File child : files) {
                    result = result && delete(child);
                }
            }
        }
        return result && dir.delete();
    }

    public static void copyTo(InputStream input, OutputStream output) throws IOException {
        copyTo(input, output, new byte[8 * 2104]);
    }

    public static void copyTo(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        try {
            int bytes = input.read(buffer);
            while (bytes >= 0) {
                output.write(buffer, 0, bytes);
                bytes = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static void copyTo(File input, File output) throws IOException {
        copyTo(new FileInputStream(input), new FileOutputStream(output));
    }
}
