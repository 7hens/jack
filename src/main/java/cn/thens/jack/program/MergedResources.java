package cn.thens.jack.program;

import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

class MergedResources extends Resources {
    private final Resources primary;
    private final Resources secondary;

    MergedResources(Resources primary, Resources secondary) {
        super(primary.getAssets(), primary.getDisplayMetrics(), primary.getConfiguration());
        this.primary = primary;
        this.secondary = secondary;
    }

    @NonNull
    @Override
    public CharSequence[] getTextArray(int id) throws NotFoundException {
        try {
            return primary.getTextArray(id);
        } catch (Throwable e) {
            return secondary.getTextArray(id);
        }
    }

    @NonNull
    @Override
    public TypedArray obtainTypedArray(int id) {
        try {
            return primary.obtainTypedArray(id);
        } catch (Throwable e) {
            return secondary.obtainTypedArray(id);
        }
    }

    @NonNull
    @Override
    public XmlResourceParser getAnimation(int id) {
        try {
            return primary.getAnimation(id);
        } catch (Throwable e) {
            return secondary.getAnimation(id);
        }
    }

    @NonNull
    @Override
    public CharSequence getText(int id) {
        try {
            return primary.getText(id);
        } catch (Throwable e) {
            return secondary.getText(id);
        }
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        try {
            return primary.getText(id, def);
        } catch (Throwable e) {
            return secondary.getText(id, def);
        }
    }

    @Override
    public DisplayMetrics getDisplayMetrics() {
        try {
            return primary.getDisplayMetrics();
        } catch (Throwable e) {
            return secondary.getDisplayMetrics();
        }
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density) {
        try {
            return primary.getDrawableForDensity(id, density);
        } catch (Throwable e) {
            return secondary.getDrawableForDensity(id, density);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        try {
            return primary.getDrawableForDensity(id, density, theme);
        } catch (Throwable e) {
            return secondary.getDrawableForDensity(id, density, theme);
        }
    }

    @Override
    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        try {
            return primary.obtainAttributes(set, attrs);
        } catch (Throwable e) {
            return secondary.obtainAttributes(set, attrs);
        }
    }

    @Override
    public int getDimensionPixelSize(int id) {
        try {
            return primary.getDimensionPixelSize(id);
        } catch (Throwable e) {
            return secondary.getDimensionPixelSize(id);
        }
    }

    @NonNull
    @Override
    public int[] getIntArray(int id) {
        try {
            return primary.getIntArray(id);
        } catch (Throwable e) {
            return secondary.getIntArray(id);
        }
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) {
        try {
            primary.getValue(id, outValue, resolveRefs);
        } catch (Throwable e) {
            secondary.getValue(id, outValue, resolveRefs);
        }
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs) {
        try {
            primary.getValue(name, outValue, resolveRefs);
        } catch (Throwable e) {
            secondary.getValue(name, outValue, resolveRefs);
        }
    }

    @NonNull
    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs) {
        try {
            return primary.getQuantityString(id, quantity, formatArgs);
        } catch (Throwable e) {
            return secondary.getQuantityString(id, quantity, formatArgs);
        }
    }

    @NonNull
    @Override
    public String getQuantityString(int id, int quantity) {
        try {
            return primary.getQuantityString(id, quantity);
        } catch (Throwable e) {
            return secondary.getQuantityString(id, quantity);
        }
    }

    @Override
    public String getResourcePackageName(int resid) {
        try {
            return primary.getResourcePackageName(resid);
        } catch (Throwable e) {
            return secondary.getResourcePackageName(resid);
        }
    }

    @NonNull
    @Override
    public String[] getStringArray(int id) {
        try {
            return primary.getStringArray(id);
        } catch (Throwable e) {
            return secondary.getStringArray(id);
        }
    }

    @Override
    public AssetFileDescriptor openRawResourceFd(int id) {
        try {
            return primary.openRawResourceFd(id);
        } catch (Throwable e) {
            return secondary.openRawResourceFd(id);
        }
    }

    @Override
    public float getDimension(int id) {
        try {
            return primary.getDimension(id);
        } catch (Throwable e) {
            return secondary.getDimension(id);
        }
    }

    @NonNull
    @Override
    public ColorStateList getColorStateList(int id) {
        try {
            return primary.getColorStateList(id);
        } catch (Throwable e) {
            return secondary.getColorStateList(id);
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public ColorStateList getColorStateList(int id, Theme theme) {
        try {
            return primary.getColorStateList(id, theme);
        } catch (Throwable e) {
            return secondary.getColorStateList(id, theme);
        }
    }

    @Override
    public boolean getBoolean(int id) {
        try {
            return primary.getBoolean(id);
        } catch (Throwable e) {
            return secondary.getBoolean(id);
        }
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        try {
            return primary.getIdentifier(name, defType, defPackage);
        } catch (Throwable e) {
            return secondary.getIdentifier(name, defType, defPackage);
        }
    }

    @NonNull
    @Override
    public CharSequence getQuantityText(int id, int quantity) {
        try {
            return primary.getQuantityText(id, quantity);
        } catch (Throwable e) {
            return secondary.getQuantityText(id, quantity);
        }
    }

    @Override
    public int getColor(int id) {
        try {
            return primary.getColor(id);
        } catch (Throwable e) {
            return secondary.getColor(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public int getColor(int id, Theme theme) {
        try {
            return primary.getColor(id, theme);
        } catch (Throwable e) {
            return secondary.getColor(id, theme);
        }
    }

    @NonNull
    @Override
    public InputStream openRawResource(int id) {
        try {
            return primary.openRawResource(id);
        } catch (Throwable e) {
            return secondary.openRawResource(id);
        }
    }

    @NonNull
    @Override
    public InputStream openRawResource(int id, TypedValue value) {
        try {
            return primary.openRawResource(id, value);
        } catch (Throwable e) {
            return secondary.openRawResource(id, value);
        }
    }

    @Override
    public Movie getMovie(int id) {
        try {
            return primary.getMovie(id);
        } catch (Throwable e) {
            return secondary.getMovie(id);
        }
    }

    @Override
    public int getInteger(int id) {
        try {
            return primary.getInteger(id);
        } catch (Throwable e) {
            return secondary.getInteger(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @Override
    public float getFloat(int id) {
        try {
            return primary.getFloat(id);
        } catch (Throwable e) {
            return secondary.getFloat(id);
        }
    }

    @Override
    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle) throws IOException, XmlPullParserException {
        try {
            primary.parseBundleExtras(parser, outBundle);
        } catch (Throwable e) {
            secondary.parseBundleExtras(parser, outBundle);
        }
    }

    @Override
    public Drawable getDrawable(int id) {
        try {
            return primary.getDrawable(id);
        } catch (Throwable e) {
            return secondary.getDrawable(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawable(int id, Theme theme) {
        try {
            return primary.getDrawable(id, theme);
        } catch (Throwable e) {
            return secondary.getDrawable(id, theme);
        }
    }

    @Override
    public String getResourceTypeName(int resid) {
        try {
            return primary.getResourceTypeName(resid);
        } catch (Throwable e) {
            return secondary.getResourceTypeName(resid);
        }
    }

    @NonNull
    @Override
    public XmlResourceParser getLayout(int id) {
        try {
            return primary.getLayout(id);
        } catch (Throwable e) {
            return secondary.getLayout(id);
        }
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Typeface getFont(int id) {
        try {
            return primary.getFont(id);
        } catch (Throwable e) {
            return secondary.getFont(id);
        }
    }

    @NonNull
    @Override
    public XmlResourceParser getXml(int id) {
        try {
            return primary.getXml(id);
        } catch (Throwable e) {
            return secondary.getXml(id);
        }
    }

    @NonNull
    @Override
    public String getString(int id) {
        try {
            return primary.getString(id);
        } catch (Throwable e) {
            return secondary.getString(id);
        }
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) {
        try {
            return primary.getString(id, formatArgs);
        } catch (Throwable e) {
            return secondary.getString(id, formatArgs);
        }
    }

    @Override
    public String getResourceName(int resid) {
        try {
            return primary.getResourceName(resid);
        } catch (Throwable e) {
            return secondary.getResourceName(resid);
        }
    }

    @Override
    public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle) throws XmlPullParserException {
        try {
            primary.parseBundleExtra(tagName, attrs, outBundle);
        } catch (Throwable e) {
            secondary.parseBundleExtra(tagName, attrs, outBundle);
        }
    }

    @Override
    public int getDimensionPixelOffset(int id) {
        try {
            return primary.getDimensionPixelOffset(id);
        } catch (Throwable e) {
            return secondary.getDimensionPixelOffset(id);
        }
    }

    @Override
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) {
        try {
            primary.getValueForDensity(id, density, outValue, resolveRefs);
        } catch (Throwable e) {
            secondary.getValueForDensity(id, density, outValue, resolveRefs);
        }
    }

    @Override
    public String getResourceEntryName(int resid) {
        try {
            return primary.getResourceEntryName(resid);
        } catch (Throwable e) {
            return secondary.getResourceEntryName(resid);
        }
    }

    @Override
    public float getFraction(int id, int base, int pbase) {
        try {
            return primary.getFraction(id, base, pbase);
        } catch (Throwable e) {
            return secondary.getFraction(id, base, pbase);
        }
    }
}
