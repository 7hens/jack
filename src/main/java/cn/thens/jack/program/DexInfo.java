package cn.thens.jack.program;

import android.os.Parcel;
import android.os.Parcelable;

import dalvik.system.DexClassLoader;

public class DexInfo implements Parcelable {
    private final String dexPath;
    private final String optimizedDirectory;
    private final String libraryDirectory;

    public DexInfo(String dexPath, String optimizedDirectory, String libraryDirectory) {
        this.dexPath = dexPath;
        this.optimizedDirectory = optimizedDirectory;
        this.libraryDirectory = libraryDirectory;
    }

    public String getDexPath() {
        return dexPath;
    }

    public String getOptimizedDirectory() {
        return optimizedDirectory;
    }

    public String getLibraryDirectory() {
        return libraryDirectory;
    }

    public static class ClassLoader extends DexClassLoader {
        public ClassLoader(java.lang.ClassLoader parent, DexInfo dexInfo) {
            super(dexInfo.dexPath, dexInfo.optimizedDirectory, dexInfo.libraryDirectory, parent);
        }
    }

    protected DexInfo(Parcel in) {
        dexPath = in.readString();
        optimizedDirectory = in.readString();
        libraryDirectory = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dexPath);
        parcel.writeString(optimizedDirectory);
        parcel.writeString(libraryDirectory);
    }

    public static final Creator<DexInfo> CREATOR = new Creator<DexInfo>() {
        @Override
        public DexInfo createFromParcel(Parcel in) {
            return new DexInfo(in);
        }

        @Override
        public DexInfo[] newArray(int size) {
            return new DexInfo[size];
        }
    };
}
