package cn.thens.jack.program;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public String toString() {
        return getClass().getName() + "(" + getPackageName() + ")";
    }
}
