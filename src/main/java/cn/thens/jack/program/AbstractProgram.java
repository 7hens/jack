package cn.thens.jack.program;

import android.text.TextUtils;

import androidx.annotation.NonNull;


abstract class AbstractProgram implements Program {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Program) {
            return TextUtils.equals(getPackageName(), ((Program) obj).getPackageName());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getPackageName().hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return getClass().getName() + "(" + getPackageName() + ")";
    }
}
